package fr.univcotedazur.skimaster.cucumber;

import org.junit.platform.suite.api.*;
import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("fr/univcotedazur/skimaster/cucumber")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "fr.univcotedazur.skimaster.cucumber")
public class CucumberRunner {}