/*
 * Automatically generated by jrpcgen 1.0.7 on 9/3/08 7:17 AM
 * jrpcgen is part of the "Remote Tea" ONC/RPC package for Java
 * See http://remotetea.sourceforge.net for details
 */
package org.epics.ioc.pdrv.vxi11.rpc;
import java.io.IOException;
import java.net.InetAddress;

import org.acplt.oncrpc.OncRpcClient;
import org.acplt.oncrpc.OncRpcClientStub;
import org.acplt.oncrpc.OncRpcException;

/**
 * The class <code>vxi11core_DEVICE_ASYNC_Client</code> implements the client stub proxy
 * for the DEVICE_ASYNC remote program. It provides method stubs
 * which, when called, in turn call the appropriate remote method (procedure).
 */
public class vxi11core_DEVICE_ASYNC_Client extends OncRpcClientStub {

    /**
     * Constructs a <code>vxi11core_DEVICE_ASYNC_Client</code> client stub proxy object
     * from which the DEVICE_ASYNC remote program can be accessed.
     * @param host Internet address of host where to contact the remote program.
     * @param protocol {@link org.acplt.oncrpc.OncRpcProtocols Protocol} to be
     *   used for ONC/RPC calls.
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public vxi11core_DEVICE_ASYNC_Client(InetAddress host, int protocol)
           throws OncRpcException, IOException {
        super(host, vxi11core.DEVICE_ASYNC, 1, 0, protocol);
    }

    /**
     * Constructs a <code>vxi11core_DEVICE_ASYNC_Client</code> client stub proxy object
     * from which the DEVICE_ASYNC remote program can be accessed.
     * @param host Internet address of host where to contact the remote program.
     * @param port Port number at host where the remote program can be reached.
     * @param protocol {@link org.acplt.oncrpc.OncRpcProtocols Protocol} to be
     *   used for ONC/RPC calls.
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public vxi11core_DEVICE_ASYNC_Client(InetAddress host, int port, int protocol)
           throws OncRpcException, IOException {
        super(host, vxi11core.DEVICE_ASYNC, 1, port, protocol);
    }

    /**
     * Constructs a <code>vxi11core_DEVICE_ASYNC_Client</code> client stub proxy object
     * from which the DEVICE_ASYNC remote program can be accessed.
     * @param client ONC/RPC client connection object implementing a particular
     *   protocol.
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public vxi11core_DEVICE_ASYNC_Client(OncRpcClient client)
           throws OncRpcException, IOException {
        super(client);
    }

    /**
     * Constructs a <code>vxi11core_DEVICE_ASYNC_Client</code> client stub proxy object
     * from which the DEVICE_ASYNC remote program can be accessed.
     * @param host Internet address of host where to contact the remote program.
     * @param program Remote program number.
     * @param version Remote program version number.
     * @param protocol {@link org.acplt.oncrpc.OncRpcProtocols Protocol} to be
     *   used for ONC/RPC calls.
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public vxi11core_DEVICE_ASYNC_Client(InetAddress host, int program, int version, int protocol)
           throws OncRpcException, IOException {
        super(host, program, version, 0, protocol);
    }

    /**
     * Constructs a <code>vxi11core_DEVICE_ASYNC_Client</code> client stub proxy object
     * from which the DEVICE_ASYNC remote program can be accessed.
     * @param host Internet address of host where to contact the remote program.
     * @param program Remote program number.
     * @param version Remote program version number.
     * @param port Port number at host where the remote program can be reached.
     * @param protocol {@link org.acplt.oncrpc.OncRpcProtocols Protocol} to be
     *   used for ONC/RPC calls.
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public vxi11core_DEVICE_ASYNC_Client(InetAddress host, int program, int version, int port, int protocol)
           throws OncRpcException, IOException {
        super(host, program, version, port, protocol);
    }

    /**
     * Call remote procedure device_abort_1.
     * @param arg1 parameter (of type Device_Link) to the remote procedure call.
     * @return Result from remote procedure call (of type Device_Error).
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public Device_Error device_abort_1(Device_Link arg1)
           throws OncRpcException, IOException {
        Device_Error result$ = new Device_Error();
        client.call(vxi11core.device_abort_1, vxi11core.DEVICE_ASYNC_VERSION, arg1, result$);
        return result$;
    }

}
// End of vxi11core_DEVICE_ASYNC_Client.java
