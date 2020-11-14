package com.greglusk.restheart.validation;

import org.apache.http.HttpResponse;

import com.greglusk.restheart.data.StorageClientResponse;
import com.greglusk.restheart.exceptions.ApiException;
import com.greglusk.restheart.exceptions.InvalidOperationException;
import com.greglusk.restheart.exceptions.UnauthorizedAccessException;
import com.greglusk.restheart.exceptions.UnknownResourceException;

public interface ResponseValidator {

	StorageClientResponse validate(HttpResponse response) throws UnauthorizedAccessException, UnknownResourceException, InvalidOperationException, ApiException;
}
