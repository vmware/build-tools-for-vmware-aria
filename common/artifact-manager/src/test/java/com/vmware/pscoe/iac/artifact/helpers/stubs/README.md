# Stubs

## What are these?
These files contain builders for Test Double objects, that should be used in vra-ng tests. You can extend them further 
if you need more customizations.

## Why do we need these?
These are not Mockito mocks since the objects are created using the Gson lib.
During our tests we were not able to json encode a Mockito mocked class. Furthermore both Gson and GsonBuilder are
final classes rendering them unmockable as well.
