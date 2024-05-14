package com.game.liquibase;

import liquibase.Scope;
import liquibase.command.CommandScope;
import liquibase.resource.ClassLoaderResourceAccessor;

public class MyLiquibaseRunner {

    public void runLiquibase() throws Exception {
        Scope.child(Scope.Attr.resourceAccessor, new ClassLoaderResourceAccessor(), () -> {
            CommandScope update = new CommandScope("update");
            update.addArgumentValue("url", "jdbc:mysql://localhost:3306");
            update.addArgumentValue("username", "root");
            update.addArgumentValue("password", "mysql");
            update.execute();
        });
    }
}
