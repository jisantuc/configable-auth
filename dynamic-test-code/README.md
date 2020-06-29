experiments in dynamic class loading
-----

To run this experiment:

- `scalac test.scala`
- `scalac dynamicLoader.scala`
- `scala Loader $(pwd)/ Foo` -- it will print that it found a class named `Foo` :tada:
- uncomment the commented part of `dynamicLoader.scala`
- `scalac dynamicLoader.scala`
- observe the error -- we can't cast to this nice class I made even though it's basically the same thing
