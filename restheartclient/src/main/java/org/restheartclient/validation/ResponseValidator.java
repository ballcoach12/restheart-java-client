package org.restheartclient.validation;

import org.apache.http.HttpResponse;
import org.restheartclient.data.StorageClientResponse;
import org.restheartclient.exceptions.ApiException;
import org.restheartclient.exceptions.InvalidOperationException;
import org.restheartclient.exceptions.UnauthorizedAccessException;
import org.restheartclient.exceptions.UnknownResourceException;

public interface ResponseValidator {

	StorageClientResponse validate(HttpResponse response) throws UnauthorizedAccessException, UnknownResourceException, InvalidOperationException, ApiException;
}
