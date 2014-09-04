package org.apache.nutch.amd.dataprovider;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.conf.Configuration;
import org.apache.nutch.parse.HTMLMetaTags;
import org.apache.nutch.parse.HtmlParseFilter;
import org.apache.nutch.parse.ParseResult;
import org.apache.nutch.protocol.Content;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DataProvider implements HtmlParseFilter {
    Configuration conf = null;
    boolean dataHuntMode = false;
    @Override
    public Configuration getConf() {
        return conf;
    }

    @Override
    public void setConf(Configuration conf) {
        this.conf = conf;
    }

    private void walk(NodeList nodes, Pattern pat,ParseResult parseResult)
    {
        for (int i = 0; i < nodes.getLength(); i++)
        {
            Node node = nodes.item(i);
            if (this.dataHuntMode && node.getNodeType() ==  Node.ELEMENT_NODE && node.getNodeName().equals("a"))
            {
                String href = node.getAttributes().getNamedItem("href").getNodeValue();
                parseResult;
                this.dataHuntMode = false;
            } else if (!this.dataHuntMode) {
                Matcher mat = pat.matcher(node.getTextContent());
                this.dataHuntMode = mat.find();
            }
            walk(node.getChildNodes(),pat,parseResult);
        }
        
    }
    
    @Override
    public ParseResult filter(Content content, ParseResult parseResult,
            HTMLMetaTags metaTags, DocumentFragment doc) {
        String str = conf.get("data.provider.regex","Data");
        this.dataHuntMode = false;
        Pattern pat = Pattern.compile(str);
        walk(doc.getChildNodes(),pat,parseResult);
        // TODO Auto-generated method stub
        return null;
    }

}
