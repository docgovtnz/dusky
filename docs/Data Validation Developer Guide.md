# Data Validation

### Design Goals
 * There needs to be an external specification of the data validation rules that's separate from the code.
 * Data validation classes will then implement these rules and be attached to the places where they need to run.
 * Changing the data validation rules should be independent of the UI code, but not necessarily independent of the Code Generator
 * Data validation rule classes have to be implemented on;
    - The GUI for usability reasons
	- On the server for security reasons
 * Data validation rule classes can be;
    - Single field
	- Cross field
	- Synchronously checked on every keypress
	- Asynchronously checked "onBlur()" - after focus leaves the field (but before the user clicks the save/update button)
	- Implemented in Angular
	- Implemented in Java


[From the Angular API docs...]
The validation status of the control. There are four possible validation status values:

VALID: This control has passed all validation checks.
INVALID: This control has failed at least one validation check.
PENDING: This control is in the midst of conducting a validation check.
DISABLED: This control is exempt from validation checks.
These status values are mutually exclusive, so a control cannot be both valid AND invalid or invalid AND disabled.


## UI Error indication

Binding some sort of visual error status is somewhat complicated. At the moment at the time this is written the code
is currently implementing a simpler approach;

* Error indicator (red/green bar on input field) is displayed based on the status: VALID or INVALID


Ideally....

Errors are displayed when the control is: invalid and (dirty or touched);	
	name.invalid && (name.dirty || name.touched)

If the input control is invalid, but the user hasn't touched the control.
 - changing the value makes it dirty (focus in the field and the user is typing)
 - touched means that the focus has left the input field (so the cursor visited and then left)

If the user hasn't touched any of the input controls and clicks the submit (e.g. save) button then the
form checks all of the validators by making all of the input controls touched before it checks to see
if the submit button is allowed to be clicked. It's something that's baked into the Angular framework for you.


# Developer Guide

1. Adding one or more Rules to an entity property
2. Adding one or more Rules to an entity itself (cross field validation)
3. Creating new rule classes
4. Adding new error messages



## Adding one or more Rules to an entity property

Adding rules to a entity property is straight forward in the src/main/resources folder there should be a
"MyEntity.validation.xml" file. This contains all of the validation rules for this entity on both the Client and Server.
 - Each property can have one or more validation rules attached to it
 - Properties that have no validation rules just don't need to appear in the xml
 - Property for nested entities can also have rules
 - TODO: rules can also be attached to the entity itself for cross field validation
 - Each Rule can have it's own unique set of parameter values to configure how the rule is to behave for this entity property

Example:

```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Class targetClassName="IslandEntity" context="Save">

    <Property name="name">
        <RequiredRule/>
    </Property>

    <Property name="upperEasting">
        <RequiredRule/>
        <NumberRangeRule min="1.0" wholeNumber="false"/>
    </Property>

...snip....
</Class>
```



## Adding one or more Rules to an entity itself (cross field validation)
 - TODO


## Creating new rule classes

Creating new Rule classes consists of the following activities;
 - Creating the Rule class itself
    - on the server
    - and on the client
 - Registering the Rule class in the relevant places so the framework can load it
 - Adding any error messages it produces


### Creating the Rule class on the Server

To create the Rule class on the server add a new class to the "com.fronde.server.services.validation.rule" package or if
there are a bunch of related rules then use sub packages.

Extend the AbstractRule base class. This will force you class to implement the "validate" method. At runtime the framework
will supply the root level object, the current property value and a "resultFactory" to your object.

Your job is to apply the Rule logic you are wanting to implement and create an error message via the "resultFactory" if
the property value fails your validation logic. You can also use any additional configuration parameters that have been
supplied to the rule class. See below for how the "addResult()" method translates the name of the error into an error
message that has been defined in the ValidationMessages.xml.


Example:

```java
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class RequiredRule extends AbstractRule {

    @Override
    public void validate(Object object, Object propertyValue, ValidationResultFactory resultFactory) {
        if(propertyValue == null || propertyValue.toString().isEmpty()) {
            resultFactory.addResult("Required");
        }
    }
}
```

So that the new Rule class can be loaded there are two places where it needs to be added so that the Framework can load
it.

In the DataValidationProperty class

```java
@XmlElements({
        @XmlElement(name = "RequiredRule", type = RequiredRule.class),
        @XmlElement(name = "NumberRangeRule", type = NumberRangeRule.class),
        @XmlElement(name = "PersonIsAccountEnabled", type = PersonIsAccountEnabled.class)
})
private List<AbstractRule> ruleList;
```


And in the AbstractRule class

```java
@JsonSubTypes({
        @JsonSubTypes.Type(value = NumberRangeRule.class, name = "NumberRangeRule"),
        @JsonSubTypes.Type(value = RequiredRule.class, name = "RequiredRule") }
)
public abstract class AbstractRule<T> {}
```

TODO: not sure why PersonIsAccountEnabled is missing and is it needed anymore


### Creating the Rule class on the Client

Unfortunately one of the downsides of having to validate both on the Client and the Server is that Rule classes need to
be implemented twice. Add the Angular client version of the rule into the "angular-client/src/app/form/validators"
directory or if there are a bunch of related rules then use sub directories.

Extend the standard Angular "Validator" interface

Implement the Rule. If your client validate doesn't work in the same way that the server rule does then you will get
problems.

When the Form is loaded for display it downloads from the server the "*Entity.validation.xml" file (as JSON) and uses
the meta-data it contains to initialise the Form with all of the Validators required.

You need to add your new Rule class into the FormService.initForm() method to convert this meta-data into a Validator
instance. [TODO: refactor this creation into a nicer approach]



## Adding Error messages


Each validation rule can produce one or more error messages. Each message template is kept in the ValidationMessages.xml
linked by the Rule's name

```xml
<Rule name="NumberRangeRule">
    <message key="LessThanMinValue">{propertyName} is less than the minimum value of {min}</message>
    <message key="GreaterThanMaxValue">{propertyName} is greater than the maximum value of {max}</message>
    <message key="WholeNumber">{propertyName} entered must be a whole number</message>
    <message key="NotANumber">{propertyName} entered is not a number</message>
</Rule>
```


The message template is a some text that will be converted into an error message at runtime on both the client and server
based on the parameter values supplied to it from validation rules. The parameter value map always include a special
parameter value for the properties "Label" and is referenced in the template by the {propertyName}

TODO: make this {propertyLabel} and actually get the Label values - this involves converting meta-data from the meta-model
into a format that the server and client can used. There's a design goal for the code-generator that it can be switched
off and leave no dependency behind.




## Rule Tips

 * Unless your Rule class is specifically about null values then usually you would ignore a null value and wrap the bulk
 of your rule logic inside of a null check. For example the NumberRangeRule ignores null values and doesn't attempt to
 create an error for null being less than the min value.

 * It's ok to generate more than on error result from a rule check, if logically it make sense. For example in the
 NumberRandRule it is possible for the Rule to create two error messages, one for being less than the minimum and one for
 not being a whole number.



## TODO:

* Data Validation added to Criteria objects??
* Changing the way errors are indicated on the input fields (Pristine, Dirty, Touched etc..)
* Error messages displaying the property's label instead of the property name
* Code generator should have a template for the *Entity.validation.xml files (generate once)
* Error messages on the client




## Links:

https://gist.github.com/jehugaleahsa/c40fb64d8613cfad8f1e1faa4c2a7e33

https://medium.com/@andrew.burton/form-validation-best-practices-8e3bec7d0549





 
 