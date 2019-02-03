# hyperml
Hyper simple and light weight XML/HTML builder for the JVM.

[![master](https://travis-ci.com/krizzdewizz/hyperml.svg?branch=master)](https://travis-ci.com/krizzdewizz/hyperml.svg?branch=master)

## Usage

Build XML/HTML in either document or ad-hoc/fluent mode.

The recommended way is to create a document by subclassing `Xml` or `Html` and overriding the `create()` method:

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
Build the XML to a string:
```
System.out.println(xml.toString());
```

or to a `Writer`:
```java
StringWriter out = new StringWriter();
xml.build(out);
System.out.println(out);
```

or to an `OutputStream`:
```java
xml.build(System.out);
```

will produce:
```html
<html>
	<body onload="doThings()">
		<h1 class="title">hello world</h1>
	</body>
</html>
```

No DOM or whatsoever is created. `$` calls emit directly to the output destination.

## HTML
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

`Html` provides a method for frequently used HTML element and constants for HTML/CSS attributes. This reduces string usage and you can leverage your editor's code completion to write the markup.

You can freely mix it with generic `$` calls.

### ad-hoc/fluent mode

For an ad-hoc document, you start from the `of` or `to` static methods instead of subclassing:

Build to string:
```java
String html = Html.of()
    .html()
        .body()
            .text("hello")
        .$()
    .$()
    .toString();
```

to a `Writer`

```java
import static hyperml.Html.$; // end-element shortcut

StringWriter writer = new StringWriter();
Html.to(writer)
    .html()
        .body("hello", $)
    .$();

String html = writer.toString();
```

will produce:
```html
<html><body>hello</body></html>
```

An ad-hoc markup is supposed to be used only once.

Document mode is favored over ad-hoc because:
- they are reusable
- with the structuring blocks `{}`, the code formatter can be used to have well nested code, whereas with ad-hoc, you must format yourself.

With a small overhead of anonymous subclasses, you can have markup quickly also in document mode:

```java
String html = new Html() {
    protected void create() {
        html();
        {
            body("hello", $);
        }
        $();
    }
}.toString();
```

## Documentation

The central piece is the `$()` method used to start/end an element.

Start an element with `$(Object elementName, Object... params)` and end an element with the parameterless `$()` overload.

### Start element

`$(Object elementName, Object... params)` takes an element name and an optional array of objects for the content of the element.

`params` are interpreted as attribute name/value pairs:
```java
$("div", "title", "hello", "class", "col-xs");
{
}
$();

// <div title="hello" class="col-xs"></div>
```

If the number of params is odd, the last one is used for the text content of the element:
```java
$("div", "title", "hello", "class", "col-xs", "world");
{
}
$();

// <div title="hello" class="col-xs">world</div>
```

If the last param is the `$` constant, the element is ended with a call to `$()`:
```java
$("div", "title", "hello", "class", "col-xs", "world", $);

// <div title="hello" class="col-xs">world</div>
```

An attribute value/text can be any object:
```java
$("div", "id", 1, "date", LocalDate.now(), $);

// <div id="1" date="2019-02-02"></div>
```

If the value is `null`, empty or `false`, the attribute is not written:
```java
$("a", "title", null, id, "", href, false, $);

// <a></a>
```

If the value is `true`, the attribute is written without a value:
```java
$("input", readonly, true);

// <input readonly>
```

Use the `"true"` and `"false"` strings if you need these values:
```java
$("input", focusable, "true");

// <input focusable="true">
```

`params` can contain structural items Array, `Iterable`, `Map` and `Stream`. These are merged into a flat array:
```java
List<Object> firstAttr = Arrays.asList("id", 1);
List<Object> moreAttrs = Arrays.asList("class", "col-xs", "href", "/");
Map<String, Object> evenMoreAttrs = new HashMap<>();
evenMoreAttrs.put("title", "hello");

$("a", firstAttr, moreAttrs, evenMoreAttrs, $);

// <a id="1" class="col-xs" href="/" title="hello"></a>
```

Instances of `Map.Entry` are treated as an attribute name/value pair.

### End element
To end an element, use the parameterless `$()` overload. The element name must not be specified, as hyperml keeps track of them using a stack. It is adviced to comment the name, especially for deeply nested documents.
```java
div();
{
    span($);
}
$(); // div
```

When the document is build, a runtime check is made to detect proper balancing of start/end calls. A runtime exception is thrown if unbalanced:
```java
Html.of().div().span("hello", $).toString();

// hyperml.HyperMlException: Missing end element call $(). Names left on stack: 'div'.
```

### Text

Output (escaped) text using the `text(Object...)` method:
```java
String name = "peter";
int age = 23;

div();
{
    text("name: ", name, ", age: ", age);
}
$();

// <div>name: peter, age: 23</div>
```

The end-element shortcut `$` can be used:
```java
div().text("name: ", name, ", age: ", age, $);
```

Text inside `script` and `style` elements is not escaped.

`raw(Object...)` outputs unescaped text:
```java
raw("let x = 1 > 2;");

// let x = 1 > 2;
```

### HTML
`Html` provides a method for frequently used HTML elements and constants for HTML/CSS attributes. This reduces string usage and you can leverage your editor's code completion to write the markup.

This `Xml`:
```java
$("span", "class", "col-xs", $);
```
can be written using `Html`:
```java
span(classs, "col-xs", $);
```

`Html` provides special support for the `class` and `style` list attributes.

`classes()` takes class/boolean pairs. The class is added to the list if the boolean value evaluates to `true`:

```java
span(classs, classes("col-xs", true, "dark", false), $);

// <span class="col-xs"></span>
```

`styles()` takes style/value pairs. The style is added to the list if the value is non-null/non-empty:

```java
span(style, styles(border, none, display, null), $);

// <span style="border:none"></span>
```

You can pass in a `Map`:
```java
Map<String, Object> moreStyles = new HashMap<>();
moreStyles.put(backgroundColor, "red");

span(style, styles(border, none, moreStyles), $);

// <span style="border:none;background-color:red"></span>
```

### CSS

`Html` provides the `css(Object...)` method to output CSS style declarations:
```java
style();
{
    css("body");
    {
        $(backgroundColor, "red");
    }
    $(); // css
}
$(); // style

// <style>body{background-color:red;}</style>
```

`css()` enters CSS mode and the `$(Object, Object...)` method treats all parameters as style property/value pairs. End CSS mode with `$()`.

You can write a declaration without a block by specifing property/value pairs. No end call needed.
```java
style();
{
    css("body", backgroundColor, "red", border, none);
}
$(); // style

// <style>body{background-color:red;border:none;}</style>
```

If a value's successor is a `Unit`, it is merged with the value:
```java
css(".col-xs", height, 10, px, width, 2, rem);

// .col-xs{height:10px;width:2rem;}
```

## Generator

Use the [hyperml generator](https://krizzdewizz.github.io/hyperml) to generate hyperml Java code from existing HTML.

## Distribution

You can download the binaries from [here](https://github.com/krizzdewizz/hyperml/releases) or via jitpack.io:

`build.gradle`:
```
repositories {
    maven { url "https://jitpack.io" }
}

dependencies {
    implementation("com.github.krizzdewizz:hyperml:1.0.1")
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
    <version>1.0.1</version>
</dependency>
```



