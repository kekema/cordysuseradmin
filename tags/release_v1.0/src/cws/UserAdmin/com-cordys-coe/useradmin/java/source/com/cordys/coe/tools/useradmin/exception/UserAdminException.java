package com.cordys.coe.tools.useradmin.exception;

import com.cordys.cpc.bsf.busobject.exception.BsfApplicationRuntimeException;

/**
 * This class is thrown when the User Admin needs to communicate an exception.
 *
 * @author  kekema
 */
@SuppressWarnings("serial")
public class UserAdminException extends BsfApplicationRuntimeException
{
	/**
     * Creates a new UserAdminException object.
     *
     * @param  message        The message for this exception.
     */
    public UserAdminException(String message)
    {
        super(message);
    }

    /**
     * Creates a new UserAdminException object.
     *
     * @param  message    The message for this exception.
     * @param  cause      The cause of the exception.
     */
    public UserAdminException(String message, Throwable cause)
    {
        super(message, cause);
    }
}