package com.cordys.coe.tools.useradmin.cordys.exception;

import com.cordys.cpc.bsf.busobject.exception.BsfApplicationRuntimeException;

/**
 * This class is thrown when the Cordys layer needs to communicate a validation exception.
 *
 * @author  kekema
 */
@SuppressWarnings("serial")
public class CordysValidationException extends BsfApplicationRuntimeException
{
	/**
     * Creates a new CordysValidationException object.
     *
     * @param  message        The message for this exception.
     */
    public CordysValidationException(String message)
    {
        super(message);
    }

    /**
     * Creates a new CordysValidationException object.
     *
     * @param  message    The message for this exception.
     * @param  cause      The cause of the exception.
     */
    public CordysValidationException(String message, Throwable cause)
    {
        super(message, cause);
    }
}