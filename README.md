## object Vector is not a value

The error message `object Vector is not a value` suggests (according to [a set of stackoverflow answers](http://stackoverflow.com/questions/9079129/object-is-not-a-value-error-in-scala)) that perhaps a Java class like `java.util.Vector` is being imported somewhere.

But where? This is a fresh project with no dependencies, as far as I can tell.

I've tried:
- Uninstall/reinstall SBT (via Homebrew)
- `rm -rf ./target`
- `sbt clean`
- `rm -rf ~/.sbt`

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


