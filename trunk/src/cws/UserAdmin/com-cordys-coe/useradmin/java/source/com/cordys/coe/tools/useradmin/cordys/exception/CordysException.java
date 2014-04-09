package com.cordys.coe.tools.useradmin.cordys.exception;

import com.cordys.cpc.bsf.busobject.exception.BsfApplicationRuntimeException;

/**
 * This class is thrown when the application needs to communicate an exception from the Cordys layer.
 *
 * @author  kekema
 */
@SuppressWarnings("serial")
public class CordysException extends BsfApplicationRuntimeException
{
	/**
     * Creates a new CordysException object.
     *
     * @param  message        The message for this exception.
     */
    public CordysException(String message)
    {
        super(message);
    }

    /**
     * Creates a new CordysException object.
     *
     * @param  message    The message for this exception.
     * @param  cause      The cause of the exception.
     */
    public CordysException(String message, Throwable cause)
    {
        super(message, cause);
    }
}