# hyperml
Hyper simple and light weight XML/HTML builder using only the method `$()`.

## Usage
Create a subclass of `Xml` or `Html` and override the `create()` method:

```java
Xml xml = new Xml() {
    protected void create() {
        
        $("html");
        {
            $("body", "onload", "doThings()");                // attribute name-value pairs
            {
                $("h1", "class", "title", "hello world", $);  // with text content, $ --> short close
            }
            $(); // body                                      // no parameters --> end element
        }
        $(); // html
        
    }
};

```

Build the XML by transforming it to a `Writer`:
```java
StringWriter out = new StringWriter();
xml.build(out);
System.out.println(out);
```

will produce:
```html
<html>
    <body onload="doThings()">
        <h1 class="title">hello world</h1>
    </body>
</html>
```

Other build destinations are `OutputStream` and the generic `ContentHandler`.

No DOM or whatsoever is created. `$` calls translate directly to SAX `ContentHandler` `startElement` / `endElement` calls.

Though nesting the `$` calls with empty braces seems like one forgot an `if` or so, it is recommended for better readability.

End-element calls (`$` with no parameters) do not need the element name, as this is handled by `Xml`. 
A runtime error will occur upon start/end call mismatch. For better maintenance, it is recommended to comment the end element call though.


## HTML support
For easier building of HTML output, subclass `Html` instead:

```java
Html html = new Html() {
    protected void create() {
        html();
        {
            style();
            {
                css(".title", "color", "red");
            }
            $(); // style

            script();
            {
                text("function doThings() { alert('done'); }");
            }
            $(); // script

            body(onload, "doThings()");
            {
                h1(classs, "title", "hello world", $);
            }
            $(); // body
        }
        $(); // html
    }
};
```

will produce:
```html
<html>
    <style>.title{color:red;}</style>
    <script>function doThings() { alert('done'); }</script>
    <body onload="doThings()">
        <h1 class="title">hello world</h1>
    </body>
</html>
```

`Html` provides a method for each known HTML element and a constant for each known HTML attribute. You can freely mix it with generic `$` calls.

## Distribution

You can download the binaries from [here](https://github.com/krizzdewizz/hyperml/releases) or via jitpack.io:

`build.gradle`:
```
repositories {
    maven { url "https://jitpack.io" }
}

dependencies {
    implementation("com.github.krizzdewizz:hyperml:1.0.0")
}
```

`pom.xml`:
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.krizzdewizz</groupId>
    <artifactId>hyperml</artifactId>
    <version>1.0.0</version>
</dependency>
```



