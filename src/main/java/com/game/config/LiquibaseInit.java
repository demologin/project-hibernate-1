package com.game.config;

import liquibase.Scope;
import liquibase.command.CommandScope;
import liquibase.resource.ClassLoaderResourceAccessor;

public class LiquibaseInit {
    public static void create(){
        try {
            Scope.child(Scope.Attr.resourceAccessor, new ClassLoaderResourceAccessor(), () -> {
                CommandScope update = new CommandScope("update");

                update.addArgumentValue("changelogFile", "db/changelog.xml");
                update.addArgumentValue("url", "jdbc:mysql://localhost:3306/rpg");
                update.addArgumentValue("username", "root");
                update.addArgumentValue("password", "mysql");

                update.execute();
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}






