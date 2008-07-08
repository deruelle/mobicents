/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mobicents.media.server.vxml.grammar;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author mitrenko
 */
public class DTMFGrammar implements Grammar {

    private static String s = "<?xml version=\"1.0\"?>\n" + 
"<grammar mode=\"dtmf\" version=\"1.0\" \n" +
 "        xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" \n" +
 "        xsi:schemaLocation=\"http://www.w3.org/2001/06/grammar \n" +
 "                            http://www.w3.org/TR/speech-grammar/grammar.xsd\"\n" +
 "        xmlns=\"http://www.w3.org/2001/06/grammar\">\n" +
"<rule id=\"digit\">\n" +
"  <one-of>\n" +
"   <item> 0 </item>\n" +
"   <item> 1 </item>\n" +
"   <item> 2 </item>\n" +
"   <item> 3 </item>\n" +
"   <item> 4 </item>\n" +
"   <item> 5 </item>\n" +
"   <item> 6 </item>\n" +
"   <item> 7 </item>\n" +
"   <item> 8 </item>\n" +
"   <item> 9 </item>\n" +
" </one-of>\n" +
" </rule>\n" +
"<rule id=\"pin\" scope=\"public\">\n" +
" <one-of>\n" +
"   <item>\n" +
"     <item repeat=\"4\"><ruleref uri=\"#digit\"/></item>\n" +
"     #\n" +
"   </item>\n" +
"   <item>\n" +
"     * 9\n" +
"   </item>\n" +
" </one-of>\n" +
"</rule>\n" +
"</grammar>";
    
    private String grammar;
    protected HashMap rules = new HashMap();
    private String rootID;
    
    public static void main(String args[]) throws Exception {
        DTMFGrammar g = new DTMFGrammar(s);
        //System.out.println("MASK=" + g.getMask("pin"));
        String exp = "([(0|1|2|3|4|5|6|7|8|9)]{4}#|\\*9)";
        System.out.println("*9".matches(g.getMask("pin")));
    }
    
    public DTMFGrammar(String url) throws ParserConfigurationException, SAXException, IOException {
        //this.grammar = grammar;

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();

        //ByteArrayInputStream in = new ByteArrayInputStream(grammar.getBytes());
        Document document = builder.parse(url);

        Element element = document.getDocumentElement();
        NodeList childs = element.getChildNodes();

        int count = childs.getLength();
        for (int i = 0; i < count; i++) {
            Node node = childs.item(i);
            if (node.getNodeName().equalsIgnoreCase("rule")) {
                rules.put(getID(node), node);
                if (node.getAttributes() != null && node.getAttributes().getNamedItem("scope") != null) {
                    rootID = getID(node);
                }
            }
        }
    }

    public String getRootRule() {
        return getMask(rootID);
    }
    
    private String getID(Node node) {
        NamedNodeMap attributes = node.getAttributes();
        return attributes.getNamedItem("id").getNodeValue();
    }

    public String getMask(String id) {
        Node node = (Node) rules.get(id);
        return new Rule(node).getMask();
    }

    private class Rule implements Serializable {

        private Node node;

        public Rule(Node node) {
            this.node = node;
        }

        public String getMask() {
            String mask = null;
            NodeList childs = node.getChildNodes();
            for (int i = 0; i < childs.getLength(); i++) {
                Node child = childs.item(i);
                if (child.getNodeName().equalsIgnoreCase("one-of")) {
                    String m = new OneOf(child).getMask();
                    if (mask == null) {
                        mask = m;
                    } else {
                        mask += "|" + m;
                    }
                }
            }
            return "(" + mask + ")";
        }
    }

    private class OneOf implements Serializable {

        private Node node;

        public OneOf(Node node) {
            this.node = node;
        }

        public String getMask() {
            NodeList items = node.getChildNodes();
            String mask = null;

            for (int i = 1; i < items.getLength(); i++) {
                Node item = items.item(i);
                if (item.getNodeName().equalsIgnoreCase("item")) {
                    if (mask == null) {
                        mask = new Item(item).getMask();
                    } else {
                        mask += "|" + new Item(item).getMask();
                    }
                }
            }
            return mask == null ? "" : mask;
        }
    }

    private class Item implements Serializable {

        private Node node;
        private int repeat = 1;

        public Item(Node node) {
            this.node = node;
            NamedNodeMap attributes = node.getAttributes();
            if (attributes != null && attributes.getNamedItem("repeat") != null) {
                repeat = Integer.parseInt(attributes.getNamedItem("repeat").getNodeValue());
            }
        }

        public String getMask() {
            NodeList childs = node.getChildNodes();
            Buffer mask = new Buffer();

            for (int i = 0; i < childs.getLength(); i++) {
                Node item = childs.item(i);
                if (item.getNodeName().equalsIgnoreCase("#text")) {
                    String s = item.getNodeValue().trim();
                    s = s.replace(" ", "");
                    if (s.length() > 0) mask.next(s);
                } else if (item.getNodeName().equalsIgnoreCase("item")) {
                    mask.append(new Item(item).getMask());
                } else if (item.getNodeName().equalsIgnoreCase("ruleref")) {
                    String uri = item.getAttributes().getNamedItem("uri").getNodeValue();
                    uri = uri.replace("#","");
                    mask.append(new Rule((Node) rules.get(uri)).getMask());
                }
            }
            
            String m = mask.toString();
            if (repeat > 1) {
                m = "[" + m + "]{" + repeat + "}";
            }
            return m;
        }
    }
    
    private class Buffer implements Serializable {
        private String s = null;
        
        public void append(String s) {
            if (this.s == null) {
                this.s = s;
            } else {
                this.s += "," + s;
            }
        }
        
        public void next(String s) {
            if (this.s == null) {
                this.s = s;
            } else 
            this.s += s; 
        }
        
        @Override
        public String toString() {
            return s.replace("*", "\\*");
        }
    }
}
