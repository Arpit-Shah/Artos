# Contributing

## Arpitos Contributor License Agreement

- You will only Submit Contributions where You have authored 100% of the content.
- You will only Submit Contributions to which You have the necessary rights. This means
  that if You are employed You have received the necessary permissions from Your employer
  to make the Contributions.
- Whatever content You Contribute will be provided under the Project License(s).

### Project Licenses

- `Arpitos` uses MIT licence.

## Commit Messages

As a general rule, the style and formatting of commit messages should follow the guidelines in
[How to Write a Git Commit Message](http://chris.beams.io/posts/git-commit/).

In addition, any commit that is related to an existing issue must reference the issue.
For example, if a commit in a pull request addresses issue \#ABC-123, it must contain the
following at the bottom of the commit message.

```
Issue: #ABC-123
```

## Pull Requests

Our Definition of Done - (/info/DEFINITION_OF_DONE.md)
offers some guidelines on what we expect from a pull request.
Feel free to open a pull request that does not fulfill all criteria, e.g. to discuss
a certain change before polishing it, but please be aware that we will only merge it
in case the DoD is met.

Please add the following lines to your pull request description:

```markdown
---
---
I hereby agree to the terms of the Arpitos Contributor License Agreement.
```

## Coding Conventions

### Naming Conventions

Whenever an acronym is included as part of a type name or method name, keep the first
letter of the acronym uppercase and use lowercase for the rest of the acronym. Otherwise,
it becomes _impossible_ to perform camel-cased searches in IDEs, and it becomes
potentially very difficult for mere humans to read or reason about the element without
reading documentation (if documentation even exists).

Consider for example a use case needing to support an HTTP URL. Calling the method
`getHTTPURL()` is absolutely horrible in terms of usability; whereas, `getHttpUrl()` is
great in terms of usability. The same applies for types `HTTPURLProvider` vs
`HttpUrlProvider`, etc.

Whenever an acronym is included as part of a field name or parameter name:

- If the acronym comes at the start of the field or parameter name, use lowercase for the
  entire acronym -- for example, `String url;`.
- Otherwise, keep the first letter of the acronym uppercase and use lowercase for the
  rest of the acronym -- for example, `String defaultUrl;`.

### Formatting

#### Code

Formatter and import order settings for Eclipse are available in the repository under
[/templates/Arpitos_Formatter_Preferance.xml](/templates/Arpitos_Formatter_Preferance.xml)

It is forbidden to use _wildcard imports_ (e.g., `import static com.arpitos.test.*;`)
in Java code.

#### Documentation

Text in `*.md` files should be wrapped at 90 characters whenever technically
possible.

In multi-line bullet point entries, subsequent lines should be indented.

### Javadoc

- Javadoc comments should be wrapped after 80 characters whenever possible.
- This first paragraph must be a single, concise sentence that ends with a period (".").
- Place `<p>` on the same line as the first line in a new paragraph and precede `<p>` with a blank line.
- Insert a blank line before at-clauses/tags.
- Favor `{@code foo}` over `<code>foo</code>`.
- Favor literals (e.g., `{@literal @}`) over HTML entities.
- Do not use `@author` tags. Instead, contributors are listed

### Tests


### Logging

- In general, logging should be used sparingly in framework.
  - _error_ (Log4J: `ERROR`): extra information (in addition to an Exception) about errors that will halt execution
  - _warn_ (Log4J: `WARN`): potential usage or configuration errors that should not halt execution
  - _info_ (Log4J: `INFO`): information the users might want to know but not by default
  - _debug_ (Log4J: `DEBUG`)
  - _trace_ (Log4J: `TRACE`)