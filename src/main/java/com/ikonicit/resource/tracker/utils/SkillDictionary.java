package com.ikonicit.resource.tracker.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SkillDictionary {

    public static final Set<String> SKILLS = new HashSet<>(Arrays.asList(

            // Programming Languages
            "java","python","c","c++","c#","go","golang","ruby","php","scala","kotlin","swift",
            "typescript","javascript","rust","dart","objective c",

            // Java Ecosystem
            "spring","spring boot","spring mvc","spring security","spring cloud",
            "hibernate","jpa","jdbc","servlet","jsp","jakarta ee",

            // Backend Frameworks
            "node","node js","nodejs","express","express js","nestjs",
            "django","flask","fastapi",
            "laravel","symfony",
            "asp.net","asp.net core",

            // Microservices / APIs
            "rest","rest api","graphql","microservices","api gateway","openapi","swagger",

            // Frontend
            "html","css","javascript","typescript",
            "react","react js","reactjs",
            "angular","angularjs",
            "vue","vuejs",
            "nextjs","nuxtjs",
            "bootstrap","tailwind","material ui",

            // Databases
            "sql","mysql","postgresql","oracle","sql server","mssql",
            "mongodb","cassandra","redis","dynamodb","firebase","elasticsearch",

            // DevOps
            "docker","kubernetes","helm",
            "terraform","ansible","chef","puppet",
            "jenkins","github actions","gitlab ci","circleci","travis ci",

            // Cloud Platforms
            "aws","azure","gcp",
            "ec2","s3","lambda","cloudfront","cloudformation",
            "rds","dynamodb","eks","aks","gke",

            // Messaging / Streaming
            "kafka","rabbitmq","activemq","zeromq",

            // Testing
            "junit","testng","mockito","hamcrest",
            "selenium","cypress","playwright","puppeteer",

            // Automation Testing
            "automation testing","api testing","ui testing",
            "selenium webdriver","rest assured","karate","cucumber","bdd","tdd",

            // Performance Testing
            "jmeter","gatling","locust","k6",

            // Mobile Development
            "android","ios","react native","flutter","xamarin",

            // Data / AI
            "machine learning","deep learning","tensorflow","pytorch",
            "pandas","numpy","scikit learn",
            "spark","hadoop","airflow",

            // Build Tools
            "maven","gradle","ant","npm","yarn",

            // Version Control
            "git","github","bitbucket","gitlab",

            // Monitoring / Logging
            "prometheus","grafana","elk","logstash","kibana","splunk",

            // Security
            "oauth","jwt","authentication","authorization","saml","openid",

            // Architecture / Concepts
            "design patterns","solid","clean architecture","mvc","mvvm","event driven",

            // Operating Systems
            "linux","unix","windows","bash","shell scripting","powershell",

            // Agile / Project Management
            "agile","scrum","kanban","jira","confluence"

    ));
}