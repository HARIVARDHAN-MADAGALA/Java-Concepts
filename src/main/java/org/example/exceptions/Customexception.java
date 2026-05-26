package org.example.exceptions;

import java.io.UncheckedIOException;
import java.sql.SQLException;

public class Customexception extends RuntimeException {

    public Customexception(String e){
        super(e);
    }
}
