
package propaganda.server;

import java.util.List;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.Action;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.4-b01
 * Generated source version: 2.2
 * 
 */
@WebService(name = "HttpWSServive", targetNamespace = "http://server.propaganda/")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface HttpWSServive {


    /**
     * 
     * @param arg1
     * @param arg0
     * @return
     *     returns java.util.List<java.lang.String>
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "processDatagram", targetNamespace = "http://server.propaganda/", className = "propaganda.server.ProcessDatagram")
    @ResponseWrapper(localName = "processDatagramResponse", targetNamespace = "http://server.propaganda/", className = "propaganda.server.ProcessDatagramResponse")
    @Action(input = "http://server.propaganda/HttpWSServive/processDatagramRequest", output = "http://server.propaganda/HttpWSServive/processDatagramResponse")
    public List<String> processDatagram(
        @WebParam(name = "arg0", targetNamespace = "")
        List<String> arg0,
        @WebParam(name = "arg1", targetNamespace = "")
        int arg1);

}