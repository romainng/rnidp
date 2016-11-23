package com.renault.rnet.idp.controller;


import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.joda.time.DateTime;
import org.opensaml.DefaultBootstrap;
import org.opensaml.common.impl.SecureRandomIdentifierGenerator;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.saml2.core.AttributeStatement;
import org.opensaml.saml2.core.AttributeValue;
import org.opensaml.saml2.core.Audience;
import org.opensaml.saml2.core.AudienceRestriction;
import org.opensaml.saml2.core.AuthnContext;
import org.opensaml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml2.core.AuthnStatement;
import org.opensaml.saml2.core.Conditions;
import org.opensaml.saml2.core.Issuer;
import org.opensaml.saml2.core.NameID;
import org.opensaml.saml2.core.Response;
import org.opensaml.saml2.core.Status;
import org.opensaml.saml2.core.StatusCode;
import org.opensaml.saml2.core.StatusMessage;
import org.opensaml.saml2.core.Subject;
import org.opensaml.saml2.core.SubjectConfirmation;
import org.opensaml.saml2.core.SubjectConfirmationData;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.XMLObjectBuilder;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.schema.XSAny;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.renault.rnet.idp.bean.ServiceProviderProperties;

/**
Utility that uses OpenSAML to carry out common SAML tasks.
* @author nfriand
*/

public class SAMLHandler
{
    /**
     * The slog4j logger uses by logback
     */
    private static final Logger log = LoggerFactory.getLogger(SAMLHandler.class);
    //private static Logger log = Logger.getLogger(SAMLHandler.class);

    private static final String CM_PREFIX = "urn:oasis:names:tc:SAML:2.0:cm:";
    
    /**
     * generate random identifier (.setID)
     */
    private static SecureRandomIdentifierGenerator generator;


    private DocumentBuilder builder;

	//private Issuer issuer;
    private String issuerURL;
	/**
    Any use of this class assures that OpenSAML is bootstrapped.
    Also initializes an ID generator.
    */
//    static 
//    {
//        try
//        {
//            DefaultBootstrap.bootstrap ();
//            generator = new SecureRandomIdentifierGenerator ();
//        }
//        catch (Exception e)
//        {
//            log.error("Cannot bootStrap OpenSAML : {}",e.getMessage());
//        }
//    }
    

   
    
 
        
 	public Assertion createAuthnAssertion(String uid, Map<String, List<String>> uidAttributes, ServiceProviderProperties serviceProvider) {
        
    	DateTime now = new DateTime ();
    	
    	//Create Issuer
        Issuer issuer = create (Issuer.class, Issuer.DEFAULT_ELEMENT_NAME);
        issuer.setValue (this.issuerURL);

        
        //Create NameID with uid of user
        NameID nameID = create (NameID.class, NameID.DEFAULT_ELEMENT_NAME);
        nameID.setValue (uid);

        //Create SubjectConfirmation
        SubjectConfirmation subjectConfirmation = create (SubjectConfirmation.class, SubjectConfirmation.DEFAULT_ELEMENT_NAME);
        subjectConfirmation.setMethod (SubjectConfirmation.METHOD_BEARER);

        //Create SubjectConfirmationData
        SubjectConfirmationData confirmationData = create (SubjectConfirmationData.class, SubjectConfirmationData.DEFAULT_ELEMENT_NAME);

        confirmationData.setNotOnOrAfter (now.plusSeconds (serviceProvider.getConditionsNotOnOrAfter()));
        confirmationData.setRecipient(serviceProvider.getConfirmationDataRecipient());
        
        subjectConfirmation.setSubjectConfirmationData(confirmationData);
                
        //Create Subject
        Subject subject = create (Subject.class, Subject.DEFAULT_ELEMENT_NAME);
        subject.setNameID (nameID);
        subject.getSubjectConfirmations ().add (subjectConfirmation);
        
        //Create Conditions
        Conditions conditions = create (Conditions.class, Conditions.DEFAULT_ELEMENT_NAME);
        conditions.setNotBefore (now.minusSeconds (serviceProvider.getConditionsNotBefore()));
        conditions.setNotOnOrAfter (now.plusMinutes(serviceProvider.getConditionsNotOnOrAfter()));

        //Create AudienceRestriction
        AudienceRestriction audienceRestriction = create(AudienceRestriction.class,AudienceRestriction.DEFAULT_ELEMENT_NAME);
        Audience audience = create(Audience.class,Audience.DEFAULT_ELEMENT_NAME);
        audience.setAudienceURI(serviceProvider.getAudienceURI());
        audienceRestriction.getAudiences().add(audience);
        
        conditions.getAudienceRestrictions().add(audienceRestriction);
        
        //Create AuthnContextClassRef
        AuthnContextClassRef authnContextClassRef = create (AuthnContextClassRef.class, AuthnContextClassRef.DEFAULT_ELEMENT_NAME);
        authnContextClassRef.setAuthnContextClassRef (AuthnContext.X509_AUTHN_CTX);
        
        AuthnContext authnContext = create (AuthnContext.class, AuthnContext.DEFAULT_ELEMENT_NAME);
        authnContext.setAuthnContextClassRef (authnContextClassRef);

       //Create AuthnStatement
        AuthnStatement authnStatement = create (AuthnStatement.class, AuthnStatement.DEFAULT_ELEMENT_NAME);
        authnStatement.setAuthnContext (authnContext);
        
        //Create Assertion
        Assertion assertion = create (Assertion.class, Assertion.DEFAULT_ELEMENT_NAME);
        assertion.setID (generator.generateIdentifier ());
        assertion.setIssueInstant (now);
        assertion.setIssuer (issuer);
        assertion.setSubject (subject);
        // added for my exple 
        assertion.setConditions(conditions);
        assertion.getStatements ().add (authnStatement);

        //adding data for JIT provisionning
        if (uidAttributes != null){
        	AttributeStatement attributeStatement = create (AttributeStatement.class, AttributeStatement.DEFAULT_ELEMENT_NAME);
            for (Entry<String, List<String>> entry : uidAttributes.entrySet ()){
            	if(entry.getValue ()!=null){
            		addAttribute (attributeStatement, entry.getKey (), entry.getValue ());
            	}
            	
            }
            assertion.getStatements ().add (attributeStatement);
        }
        
        
        return assertion;
	}

	/**
    Initialize JAXP DocumentBuilder instance for later use and reuse.
    */
    public SAMLHandler ()
    {
        this (null);
    }
    
    /**
    Initialize JAXP DocumentBuilder instance for later use and reuse, and
    establishes an issuer URL.
    
    @param issuerURL This will be used in all generated assertions
    */
    public SAMLHandler (String issuerURL)
    {
        try
        {
        	this.issuerURL=issuerURL;
        	//bootstrap API
        	DefaultBootstrap.bootstrap ();
        	
        	//Create unique instance of DocumentBuilderFactory
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance ();
            factory.setNamespaceAware (true);
            builder = factory.newDocumentBuilder ();

            //Create unique instance of Issuer at startUp
            //issuer = create (Issuer.class, Issuer.DEFAULT_ELEMENT_NAME);
            //issuer.setValue (issuerURL);
            
            //Create unique instance of SecureRandomIdentifierGenerator
            generator = new SecureRandomIdentifierGenerator ();
        }
        catch (Exception ex)
        {
            ex.printStackTrace ();
        }
    }
    
    /**
    <u>Slightly</u> easier way to create objects using OpenSAML's 
    builder system.
    */
    // cast to SAMLObjectBuilder<T> is caller's choice    
    @SuppressWarnings ("unchecked")
    public <T> T create (Class<T> cls, QName qname)
    {
        return (T) ((XMLObjectBuilder) 
            Configuration.getBuilderFactory ().getBuilder (qname))
                .buildObject (qname);
    }
    
    /**
    Helper method to add an XMLObject as a child of a DOM Element.
    */
    public static Element addToElement (XMLObject object, Element parent)
        throws IOException, MarshallingException, TransformerException
    {
        Marshaller out = Configuration.getMarshallerFactory ().getMarshaller (object);
        return out.marshall (object, parent);
    }

    /**
    Helper method to get an XMLObject as a DOM Document.
    */
    public Document asDOMDocument (XMLObject object)
        throws IOException, MarshallingException, TransformerException
    {
        Document document = builder.newDocument ();
        Marshaller out = Configuration.getMarshallerFactory ().getMarshaller (object);
        out.marshall (object, document);
        return document;
    }

    /**
    Helper method to pretty-print any XML object to a file.
    */
//    public void printToFile (XMLObject object, String filename)
//        throws IOException, MarshallingException, TransformerException
//    {
//        Document document = asDOMDocument (object);
//        
//        String result = PrettyPrinter.prettyPrint (document);
//        if (filename != null)
//        {
//            PrintWriter writer = new PrintWriter (new FileWriter (filename));
//            writer.println (result);
//            writer.close ();
//        }
//        else
//            System.out.println (result);
//    }

    /**
    Helper method to pretty-print any XML object.
    */
    public String prettyPrint (XMLObject object)
        throws IOException, MarshallingException, TransformerException
    {
        Document document = asDOMDocument (object);
        
        return PrettyPrinter.prettyPrint (document);

    }

    /**
    Helper method to pretty-print any XML object with signature .
    */
    public String prettyPrintSigneObject (XMLObject object)
        throws IOException, MarshallingException, TransformerException
    {
        Document document = asDOMDocument (object);
        
        //default transformer
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        StreamResult result = new StreamResult(new StringWriter());
        DOMSource source = new DOMSource(document);
        transformer.transform(source, result);
        
        //Transform XML object as string without signature corruption   
        return result.getWriter().toString();
    }

    /**
    Helper method to read an XML object from a DOM element.
    */
    public static XMLObject fromElement (Element element)
        throws IOException, UnmarshallingException, SAXException
    {
        return Configuration.getUnmarshallerFactory ()
            .getUnmarshaller (element).unmarshall (element);    
    }

    /**
    Helper method to read an XML object from a file.
    */
    public XMLObject readFromFile (String filename)
        throws IOException, UnmarshallingException, SAXException
    {
        return fromElement (builder.parse (filename).getDocumentElement ());    
    }

    /**
    Helper method to spawn a new Issuer element based on our issuer URL.
    */
//    public Issuer spawnIssuer ()
//    {
//        Issuer result = null;
//        if (issuerURL != null)
//        {
//            result = create (Issuer.class, Issuer.DEFAULT_ELEMENT_NAME);
//            result.setValue (issuerURL);
//        }
//        
//        return result;
//    }
    
    /**
    Returns a SAML subject.
    
    @param username The subject name
    @param format If non-null, we'll set as the subject name format
    @param confirmationMethod If non-null, we'll create a SubjectConfirmation
        element and use this as the Method attribute; must be "sender-vouches"
        or "bearer", as HOK would require additional parameters and so is NYI
    */
//    public Subject createSubject (String username, String format, String confirmationMethod)
//    {
//        NameID nameID = create (NameID.class, NameID.DEFAULT_ELEMENT_NAME);
//        nameID.setValue (username);
//        if (format != null)
//            nameID.setFormat (format);
//        
//        Subject subject = create (Subject.class, Subject.DEFAULT_ELEMENT_NAME);
//        subject.setNameID (nameID);
//        
//        if (confirmationMethod != null)
//        {
//            SubjectConfirmation confirmation = create 
//                (SubjectConfirmation.class, 
//                    SubjectConfirmation.DEFAULT_ELEMENT_NAME);
//            confirmation.setMethod (CM_PREFIX + confirmationMethod);
//            
//            subject.getSubjectConfirmations ().add (confirmation);
//        }
//
//        return subject;        
//    }
    
    /**
    Returns a SAML assertion with generated ID, current timestamp, given
    subject, and simple time-based conditions.
    
    @param subject Subject of the assertion
    */
//    public Assertion createAssertion (Subject subject)
//    {
//        Assertion assertion = create (Assertion.class, Assertion.DEFAULT_ELEMENT_NAME);
//        assertion.setID (generator.generateIdentifier ());
//
//        DateTime now = new DateTime ();
//        assertion.setIssueInstant (now);
//        
//        if (issuerURL != null)
//            assertion.setIssuer (spawnIssuer ());
//        
//        assertion.setSubject (subject);
//
//        Conditions conditions = create (Conditions.class, Conditions.DEFAULT_ELEMENT_NAME);
//        conditions.setNotBefore (now.minusSeconds (10));
//        conditions.setNotOnOrAfter (now.plusMinutes (30));
//        assertion.setConditions (conditions);
//
//        return assertion;
//    }
    
    /**
    Helper method to generate a response, based on a pre-built assertion.
    */
    public Response createResponse (Assertion assertion)
        throws IOException, MarshallingException, TransformerException
    {
        return createResponse (assertion, null);
    }
    
    /**
    Helper method to generate a shell response with a given status code
    and query ID.
    */
    public Response createResponse (String statusCode, String inResponseTo)
        throws IOException, MarshallingException, TransformerException
    {
        return createResponse (statusCode, null, inResponseTo);
    }
    
    /**
    Helper method to generate a shell response with a given status code,
    status message, and query ID.
    */
    public Response createResponse (String statusCode, String message, String inResponseTo)
        throws IOException, MarshallingException, TransformerException
    {
        Response response = create (Response.class, Response.DEFAULT_ELEMENT_NAME);
        
        response.setID (generator.generateIdentifier ());
        
        if (inResponseTo != null)
            response.setInResponseTo (inResponseTo);
            
        DateTime now = new DateTime ();
        response.setIssueInstant (now);
        
//        if (issuerURL != null)
//            response.setIssuer (spawnIssuer ());
        
        StatusCode statusCodeElement = create (StatusCode.class, StatusCode.DEFAULT_ELEMENT_NAME);
        statusCodeElement.setValue (statusCode);
        
        Status status = create (Status.class, Status.DEFAULT_ELEMENT_NAME);
        status.setStatusCode (statusCodeElement);
        response.setStatus (status);

        if (message != null)
        {
            StatusMessage statusMessage = create (StatusMessage.class, StatusMessage.DEFAULT_ELEMENT_NAME);
            statusMessage.setMessage (message);
            status.setStatusMessage (statusMessage);
        }
        
        return response;
    }
    
    /**
    Helper method to generate a response, based on a pre-built assertion
    and query ID.
    */
    public Response createResponse (Assertion assertion, String inResponseTo)
        throws IOException, MarshallingException, TransformerException
    {
        Response response = createResponse (StatusCode.SUCCESS_URI, inResponseTo);

        response.getAssertions ().add (assertion);
        
        return response;
    }
    
    /**
    Returns a SAML authentication assertion.
    
    @param subject The subject of the assertion
    @param authnCtx The "authentication context class reference",
      e.g. AuthnContext.PPT_AUTHN_CTX
    */
//    public Assertion createAuthnAssertion (Subject subject, String authnCtx)
//    {
//        Assertion assertion = createAssertion (subject);
//
//        AuthnContextClassRef ref = create (AuthnContextClassRef.class, 
//            AuthnContextClassRef.DEFAULT_ELEMENT_NAME);
//        ref.setAuthnContextClassRef (authnCtx);
//        
//        // As of this writing, OpenSAML doesn't model the wide range of
//        // authentication context namespaces defined in SAML 2.0.
//        // For a real project we'd probably move on to 
//        //    XSAny objects, setting QNames and values each-by-each
//        //    a JAXB mapping of the required schema
//        //    DOM-building
//        // For classroom purposes the road ends here ...
//        
//        AuthnContext authnContext = create 
//            (AuthnContext.class, AuthnContext.DEFAULT_ELEMENT_NAME);
//        authnContext.setAuthnContextClassRef (ref);
//
//        AuthnStatement authnStatement = create 
//            (AuthnStatement.class, AuthnStatement.DEFAULT_ELEMENT_NAME);
//        authnStatement.setAuthnContext (authnContext);
//        
//        assertion.getStatements ().add (authnStatement);
//        
//        return assertion;
//    }
    
    /**
    Adds a SAML attribute to an attribute statement.
    
    @param statement Existing attribute statement
    @param name Attribute name
    @param list Attribute value
    */
    public void addAttribute (AttributeStatement statement, String name, List<String> attributeValues)
    {
        // Build attribute values as XMLObjects;
        //  there is an AttributeValue interface, but it's apparently dead code
        final XMLObjectBuilder builder = Configuration.getBuilderFactory ().getBuilder (XSAny.TYPE_NAME);

        Attribute attribute = create (Attribute.class, Attribute.DEFAULT_ELEMENT_NAME);
        attribute.setName (name);

        for (String attributeValue : attributeValues){
            XSAny valueElement = (XSAny) builder.buildObject (AttributeValue.DEFAULT_ELEMENT_NAME);
            valueElement.setTextContent (attributeValue);

            attribute.getAttributeValues ().add (valueElement);
        }

        statement.getAttributes ().add (attribute);
    }

    /**
    Returns a SAML attribute assertion.
    
    @param subject Subject of the assertion
    @param attributes Attributes to be stated (may be null)
    */
//    public Assertion createAttributeAssertion (Subject subject, Map<String,String> attributes)
//    {
//        Assertion assertion = createAssertion (subject);
//        
//        AttributeStatement statement = create (AttributeStatement.class, AttributeStatement.DEFAULT_ELEMENT_NAME);
//        if (attributes != null)
//            for (Map.Entry<String,String> entry : attributes.entrySet ())
//                addAttribute (statement, entry.getKey (), entry.getValue ());
//
//        assertion.getStatements ().add (statement);
//
//        return assertion;
//    }
}
