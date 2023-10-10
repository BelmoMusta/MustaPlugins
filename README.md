# Intellij-REST-Creator-Plugin

This plugin helps to generate Spring REST controller endpoints by just using the signature of that endpoint.
## Example 
In the intellij idea editor, writing the endpoint signature and press `ctrl + space` ( or whatever key stroke for autocompletion), 
this action will provide an option to create the equivalent REST endpoint.

```shell
GET /user/{id}
```
will generate the following: 

```java
    /*
     * Generated from 'GET user/{id}'
     */
    @GetMapping("user/{id}")
    public ResponseEntity<?> getUserById(@PathVariable("id") Long id) {
        //FIXME: Provide a valid implementation for this method
        throw new RuntimeException("Not implemented yet");
    } 
```
The imports section is updated as well, to include introduced references by the created method.