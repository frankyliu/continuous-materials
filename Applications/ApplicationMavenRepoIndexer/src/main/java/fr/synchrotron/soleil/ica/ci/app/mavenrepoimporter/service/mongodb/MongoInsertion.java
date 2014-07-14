package fr.synchrotron.soleil.ica.ci.app.mavenrepoimporter.service.mongodb;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import fr.synchrotron.soleil.ica.ci.lib.mongodb.domainobjects.artifact.ArtifactDocument;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Map;

/**
 * @author Gregory Boissinot
 */
public class MongoInsertion {

    private MongoTemplate mongoTemplate;

    private ObjectMapper objectMapper;

    public MongoInsertion(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
        this.objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

    }

    public void insert(ArtifactDocument artifactDocument) {

        Query query = new Query();
        query.addCriteria(Criteria.where("org").is(artifactDocument.getOrg()))
                .addCriteria(Criteria.where("name").is(artifactDocument.getName()))
                .addCriteria(Criteria.where("status").is(artifactDocument.getStatus()))
                .addCriteria(Criteria.where("version").is(artifactDocument.getVersion()));

        final BasicDBObject basicDBObject = new BasicDBObject();
        Map<String, Object> map = objectMapper.convertValue(artifactDocument, Map.class);
        basicDBObject.putAll(map);

        Update update = Update.fromDBObject(basicDBObject);
        mongoTemplate.upsert(query, update, "artifacts");


    }
}
