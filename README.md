**OSMonaut** is a Java framework that makes it easy to parse OpenStreetMap data from binary files (PBF). The main feature is that it always returns complete OSM objects. That means a relation comes with all the members and not just member IDs and ways come with all the nodes including the tags and location of those nodes.

To achieve this it is necessary to implement the two methods of the IOsmonautReceiver interface: `needsEntity()` and `foundEntity()`. `needsEntity()` helps reducing the amount of memory needed. For example if a relation is not needed there is no need to keep all the member ways and nodes in memory. `foundEntity()` is called when all the members of the entity have been prepared and includes the complete entity.

OSMonaut offers basic geometric functions and can handle multipolygons. Multithreading is used for parsing the PBF file and a low memory mode is available that stores the caches on disk. Super-relations are not yet supported.

## Sample ##

Here is a small sample that prints the names and center coordinates of all lakes:

```java
// Set which OSM entities should be scanned (only nodes and ways in this case)
EntityFilter filter = new EntityFilter(true, true, false);
	
// Set the binary OSM source file
Osmonaut naut = new Osmonaut("planet.osm.pbf", filter);
	
// Start scanning by implementing the interface
naut.scan(new IOsmonautReceiver() {
	@Override
	public boolean needsEntity(EntityType type, Tags tags) {
		// Only lakes with names
		return (tags.hasKeyValue("natural", "water") && tags.hasKey("name"));
	}
	
	@Override
	public void foundEntity(Entity entity) {
		// Print name and center coordinates
		String name = entity.getTags().get("name");
		System.out.println(name + ": " + entity.getCenter());
	}
});
```

[→ Full documentation](https://morbz.github.io/OSMonaut/)