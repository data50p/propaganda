
package propaganda.server;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the propaganda.server package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _ProcessDatagram_QNAME = new QName("http://server.propaganda/", "processDatagram");
    private final static QName _ProcessDatagramResponse_QNAME = new QName("http://server.propaganda/", "processDatagramResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: propaganda.server
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ProcessDatagramResponse }
     * 
     */
    public ProcessDatagramResponse createProcessDatagramResponse() {
        return new ProcessDatagramResponse();
    }

    /**
     * Create an instance of {@link ProcessDatagram }
     * 
     */
    public ProcessDatagram createProcessDatagram() {
        return new ProcessDatagram();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ProcessDatagram }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.propaganda/", name = "processDatagram")
    public JAXBElement<ProcessDatagram> createProcessDatagram(ProcessDatagram value) {
        return new JAXBElement<ProcessDatagram>(_ProcessDatagram_QNAME, ProcessDatagram.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ProcessDatagramResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.propaganda/", name = "processDatagramResponse")
    public JAXBElement<ProcessDatagramResponse> createProcessDatagramResponse(ProcessDatagramResponse value) {
        return new JAXBElement<ProcessDatagramResponse>(_ProcessDatagramResponse_QNAME, ProcessDatagramResponse.class, null, value);
    }

}
