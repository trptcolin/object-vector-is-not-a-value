## object Vector is not a value

### solved (see details below)

The error message `object Vector is not a value` suggests (according to [a set of stackoverflow answers](http://stackoverflow.com/questions/9079129/object-is-not-a-value-error-in-scala)) that perhaps a Java class like `java.util.Vector` is being imported somewhere.

But where? This is a fresh project with no dependencies, as far as I can tell.

I do realize this error doesn't affect everyone (and I'm able to get it working in an Ubuntu VM), so I'm wondering what could be different about my external-to-this-project setup that makes this issue non-repeatable.

These things make it work:
- Making `Vector` package-qualified to be `scala.collection.immutable.Vector` (see comments in `src/main/scala/hw.scala`)
- Adding a real package declaration (instead of the default package - see the comments in `src/main/scala/hw.scala`)
- Running the scala REPL and doing a `:load "./src/main/scala/hw.scala"`
- Use something else, like `Seq`, `List`, or `Set` instead of `Vector` (`List` and `Set` also live in java.util)

This is really a learning exercise, since I have a workaround for the actual use case.

I've tried:
- Uninstall/reinstall SBT (via Homebrew)
- `rm -rf ./target`
- `sbt clean`
- `rm -rf ~/.sbt`
- using both Java 1.7 and 1.8

with no change to the behavior below:

```
colin:hello/ (master) $ ls -l $(which sbt)
lrwxr-xr-x  1 colin  admin  29 Oct 11 14:45 /usr/local/bin/sbt -> ../Cellar/sbt/0.13.12/bin/sbt
colin:hello/ (master) $ sbt clean run
[info] Loading project definition from /Users/colin/Practice/hello/project
[info] Set current project to hello (in build file:/Users/colin/Practice/hello/)
[success] Total time: 0 s, completed Oct 11, 2016 3:42:37 PM
[info] Updating {file:/Users/colin/Practice/hello/}hello...
[info] Resolving jline#jline;2.12.1 ...
[info] Done updating.
[info] Compiling 1 Scala source to /Users/colin/Practice/hello/target/scala-2.11/classes...
[error] /Users/colin/Practice/hello/src/main/scala/hw.scala:4: object Vector is not a value
[error]                 val xs = Vector(1,2,3)
[error]                          ^
[error] one error found
[error] (compile:compile) Compilation failed
[error] Total time: 3 s, completed Oct 11, 2016 3:42:40 PM
```

Going a step further, via the stream files I found listed under `./target` via:

```
colin:hello/ (master) $ find target/streams/compile/ -type f| xargs cat
```

I found a `scalac` invocation that also exhibits the problem for me:

```
colin:object-vector-is-not-a-value/ (master) $ scalac -bootclasspath /Library/Java/JavaVirtualMachines/jdk1.8.0_65.jdk/Contents/Home/jre/lib/resources.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_65.jdk/Contents/Home/jre/lib/rt.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_65.jdk/Contents/Home/jre/lib/sunrsasign.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_65.jdk/Contents/Home/jre/lib/jsse.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_65.jdk/Contents/Home/jre/lib/jce.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_65.jdk/Contents/Home/jre/lib/charsets.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_65.jdk/Contents/Home/jre/lib/jfr.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_65.jdk/Contents/Home/jre/classes:/Users/colin/.ivy2/cache/org.scala-lang/scala-library/jars/scala-library-2.11.8.jar -classpath /Users/colin/Practice/object-vector-is-not-a-value/target/scala-2.11/classes /Users/colin/Practice/object-vector-is-not-a-value/src/main/scala/hw.scala
/Users/colin/Practice/object-vector-is-not-a-value/src/main/scala/hw.scala:9: error: object Vector is not a value
    val xs = Vector(1,2,3)
             ^
one error found
```

No change if I:
- downgrade to `jdk1.7.0_45` instead
- move everything from the bootclasspath to the classpath
- remove everything else from the classpath except the custom code

Things *do* work if I replace `Vector` with `Set`, presumably since that's explicitly brought into scope via the [Predef](http://www.scala-lang.org/files/archive/api/2.11.8/#scala.Predef$).

Updated smallest reproduced case:

```
colin:object-vector-is-not-a-value/ (master) $ scalac -version
Scala compiler version 2.11.8 -- Copyright 2002-2016, LAMP/EPFL
colin:object-vector-is-not-a-value/ (master) $ scalac ./src/main/scala/hw.scala
./src/main/scala/hw.scala:10: error: object Vector is not a value
    val xs = Vector(1,2,3)
             ^
one error found
```

If I use the Java constructor (`new Vector`), I get more info on what this class is:

```
src/main/scala/hw.scala:9: error: overloaded method constructor Vector with alternatives:
  (x$1: Array[Double])Vector <and>
  (x$1: Int)Vector
 cannot be applied to (Int, Int, Int)
                val xs = new Vector(1,2,3)
                         ^
one error found
```

These constructor options do *not* match `java.util.Vector`, so this must be something else.

What Vector class has 2 constructors of these types?

```
colin:object-vector-is-not-a-value/ (master) $ scalac -verbose -deprecation -explaintypes src/main/scala/hw.scala
```

Too much output to show, in the README, but here's [the gist of it](https://gist.github.com/trptcolin/d3e157b0d50fdbc0ac2ca23a8338ab79).

Searched for "Vector" in that gist, found

```
[loaded class file /Users/colin/Library/Java/Extensions/algs4.jar(Vector.class) in 1ms]
```

A Coursera algorithms class had me put this jar there back in 2012, and it's got this mysterious Vector class.
All its classes lives in the default package.

So this explains why:
- it's limited to my machine (probably few others have this extension installed)
- it only affects the default package (goes away when I add a package declaration)

It doesn't explain to me why it worked in the REPL - maybe Scala types get precedence over Java classes in the interpreter somehow?
