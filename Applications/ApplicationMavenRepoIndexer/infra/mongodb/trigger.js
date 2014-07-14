var conn = new Mongo("localhost:27017");
var repoDB = conn.getDB("repo");

var coll = db.oplog.rs;
var lastTimeStamp = coll.find().sort({ '$natural': -1 })[0].ts;

while (1) {
    cursor = coll.find({ ts: { $gt: lastTimeStamp } });
    // tailable
    cursor.addOption(2);
    // await data
    cursor.addOption(32);

    while (cursor.hasNext()) {
        var doc = cursor.next();
        lastTimeStamp = doc.ts;
        printjson(doc);
    }
}
