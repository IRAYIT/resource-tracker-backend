package com.ikonicit.resource.tracker.utils;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SkillDictionary {

    public static final Set<String> SKILLS = new HashSet<>(Arrays.asList(

            // Programming Languages
            "java","python","c","c++","c#","go","golang","ruby","php","scala","kotlin","swift","typescript",

            // Java Ecosystem
            "spring","spring boot","spring mvc","spring security","spring cloud",
            "hibernate","jpa","jdbc","servlet","jsp",

            // Backend
            "rest","rest api","graphql","microservices","api gateway",

            // Frontend
            "html","css","javascript","typescript",
            "react","angular","vue","bootstrap","tailwind",

            // Databases
            "sql","mysql","postgresql","oracle","sql server",
            "mongodb","cassandra","redis","dynamodb",

            // DevOps
            "docker","kubernetes","terraform","ansible",
            "jenkins","github actions","gitlab ci",

            // Cloud
            "aws","azure","gcp","ec2","s3","lambda","rds",

            // Messaging
            "kafka","rabbitmq","activemq",

            // Testing
            "junit","mockito","selenium","cucumber","testng","postman",

            // Build Tools
            "maven","gradle","npm","yarn",

            // Version Control
            "git","github","bitbucket","gitlab",

            // Architecture
            "design patterns","solid","mvc","microservices",

            // Security
            "oauth","jwt","authentication","authorization",

            // Data
            "machine learning","tensorflow","pytorch","pandas","numpy","spark","hadoop",

            // OS
            "linux","unix","bash","shell scripting",

            // Agile
            "agile","scrum","kanban","jira"

    ));
}