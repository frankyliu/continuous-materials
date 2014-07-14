package fr.synchrotron.soleil.ica.ci.app.mavenrepoimporter.service;

import org.apache.maven.index.Indexer;
import org.apache.maven.index.context.IndexCreator;
import org.apache.maven.index.context.IndexingContext;
import org.apache.maven.index.updater.*;
import org.apache.maven.wagon.Wagon;
import org.apache.maven.wagon.events.TransferEvent;
import org.apache.maven.wagon.events.TransferListener;
import org.apache.maven.wagon.observers.AbstractTransferListener;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainer;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Gregory Boissinot
 */
public class MavenIndexerRetriever {

    public IndexingContext getMavenIndex(String repoURL) throws Exception {

        System.out.println("Fetching from the repo " + repoURL);

        PlexusContainer plexusContainer = new DefaultPlexusContainer();
        Indexer indexer = plexusContainer.lookup(Indexer.class);

        int repoID = repoURL.hashCode();

        File centralLocalCache = new File("target/" + repoID + "/central-cache");
        File centralIndexDir = new File("target/" + repoID + "/central-index");

        // Creators we want to use (search for fields it defines)
        List<IndexCreator> indexers = new ArrayList<IndexCreator>();
        indexers.add(plexusContainer.lookup(IndexCreator.class, "min"));

        // Create context for central repository index
        IndexingContext centralContext = indexer.createIndexingContext("central-context", "central", centralLocalCache, centralIndexDir,
                repoURL, null, true, true, indexers);


        System.out.println("Updating Index...");
        System.out.println("This might take a while on first run, so please be patient!");
        // Create ResourceFetcher implementation to be used with IndexUpdateRequest
        // Here, we use Wagon based one as shorthand, but all we need is a ResourceFetcher implementation
        TransferListener listener = new AbstractTransferListener() {
            public void transferStarted(TransferEvent transferEvent) {
                System.out.print("  Downloading " + transferEvent.getResource().getName());
            }

            public void transferProgress(TransferEvent transferEvent, byte[] buffer, int length) {
            }

            public void transferCompleted(TransferEvent transferEvent) {
                System.out.println(" - Done");
            }
        };

        Wagon httpWagon = plexusContainer.lookup(Wagon.class, "http");
        IndexUpdater indexUpdater = plexusContainer.lookup(IndexUpdater.class);

        ResourceFetcher resourceFetcher = new WagonHelper.WagonFetcher(httpWagon, listener, null, null);

        Date centralContextCurrentTimestamp = centralContext.getTimestamp();
        IndexUpdateRequest updateRequest = new IndexUpdateRequest(centralContext, resourceFetcher);
        IndexUpdateResult updateResult = indexUpdater.fetchAndUpdateIndex(updateRequest);
        if (updateResult.isFullUpdate()) {
            System.out.println("Full update happened!");
        } else if (updateResult.getTimestamp().equals(centralContextCurrentTimestamp)) {
            System.out.println("No update needed, index is up to date!");
        } else {
            System.out.println("Incremental update happened, change covered " + centralContextCurrentTimestamp
                    + " - " + updateResult.getTimestamp() + " period.");
        }

        System.out.println();
        return centralContext;
    }

}
