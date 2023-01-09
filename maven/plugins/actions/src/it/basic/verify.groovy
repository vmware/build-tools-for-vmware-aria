File packageFile = new File( basedir, "target/org.example.basic-1.0.0.package" )
assert packageFile.exists()
def zip = new java.util.zip.ZipFile(packageFile)

def actionInfoEntry = zip.getEntry("elements/f91c43b5-98fe-3e81-9a7f-c1693c384570/info")
assert  actionInfoEntry != null
def entries = zip.entries().iterator()
assert entries.count{ it.name.startsWith("elements/") && it.name.endsWith("/info") } == 1

def slurper = new groovy.util.XmlSlurper()
slurper.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false)
slurper.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)

def dunesMetaInf = slurper.parse(zip.getInputStream(zip.getEntry("dunes-meta-inf")))
def pkgName = dunesMetaInf.'*'.find { node -> node.@key == 'pkg-name' }

assert pkgName.text() == 'org.example.basic-1.0.0'