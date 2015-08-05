package net.opticode.xsdtools.generator;

// Java Classes
import java.io.*;
import java.util.*;

// Velocity Classes
import org.apache.velocity.app.*;
import org.apache.velocity.*;
import org.apache.velocity.exception.*;

// Local Classes
import net.opticode.xsdtools.parser.*;
import net.opticode.xsdtools.om.*;

public class Generator
{
    private String elementTemplateFile = "templates/element.vm";
    private String mainTemplateFile = "templates/main.vm";

    public Generator () throws Exception
    {
        Velocity.init();
    }

    public Generator (String elementTemplate, String mainTemplate) throws Exception
    {
        elementTemplateFile = elementTemplate;
        mainTemplateFile = mainTemplate;
        Velocity.init();
    }


    public void execute (InputStream is, String outputDirectory, String packageName) throws Exception
    {
        Template elementTemplate = Velocity.getTemplate(elementTemplateFile);
        Template mainTemplate = Velocity.getTemplate(mainTemplateFile);

        XSDParser xsdp = new XSDParser();
        Elements elements = xsdp.parse (is);

        VelocityContext context = new VelocityContext();
        context.put ("elements",elements);
        context.put ("package",packageName);

        String dir = outputDirectory + "/" + packageName.replace('.','/') + "/";

        System.out.println ("Generating to package "+packageName);
        System.out.println ("Generating to directory "+dir);

        File f = new File (dir);
        f.mkdirs();

        FileWriter fw = new FileWriter (dir+"Parser.java");
        mainTemplate.merge (context, fw);
        fw.flush();
        fw.close();

        for (int i=0; i<elements.size(); i++)
        {
            if (i==0 || elements.get(i).hasChildren())
            {
                context = new VelocityContext();
                context.put ("element",elements.get(i));
                context.put ("package",packageName);

                fw = new FileWriter (dir+elements.get(i).getClassName()+"handler.java");
                elementTemplate.merge (context, fw);
                fw.flush();
                fw.close();

            }
        }

    }

    public static void main (String args[]) throws Exception
    {
        Generator g = new Generator();
        FileInputStream fis = new FileInputStream (args[0]);
        g.execute (fis, "output", args[1]);
        fis.close();
    }

}
