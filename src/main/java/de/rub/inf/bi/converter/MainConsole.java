package de.rub.inf.bi.converter;

import de.rub.inf.bi.misc.RandomString;
import fr.mines_stetienne.ci.sparql_generate.SPARQLExt;
import fr.mines_stetienne.ci.sparql_generate.engine.PlanFactory;
import fr.mines_stetienne.ci.sparql_generate.engine.RootPlan;
import fr.mines_stetienne.ci.sparql_generate.query.SPARQLExtQuery;
import fr.mines_stetienne.ci.sparql_generate.stream.LocationMapperAccept;
import fr.mines_stetienne.ci.sparql_generate.stream.SPARQLExtStreamManager;
import fr.mines_stetienne.ci.sparql_generate.utils.ContextUtils;
import jakarta.ws.rs.core.Response;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.sparql.util.Context;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.*;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.nio.file.Paths;

/**
 * Main class.
 *
 */
public class MainConsole {

    /**
     * Main method.
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        if (args.length > 2) {
            System.out.println("Called convert function");
            String inputXMLFile = args[0];
            String queryRQGFile = args[1];
            String outputTTLFile = args[2];
            System.out.println(inputXMLFile);
            System.out.println(queryRQGFile);
            System.out.println(outputTTLFile);
            Model overallModel = ModelFactory.createDefaultModel();
            LocationMapperAccept mapper = new LocationMapperAccept();
            mapper.addAltEntry("http://example.org/document#document0.xml", inputXMLFile);

            SPARQLExtStreamManager sm = SPARQLExtStreamManager.makeStreamManager(mapper);
            Context context = ContextUtils.build()
                    .setStreamManager(sm)
                    .build();


                SPARQLExtQuery query = (SPARQLExtQuery) QueryFactory.read(queryRQGFile, SPARQLExt.SYNTAX);
                RootPlan plan = PlanFactory.create(query);
                Model model = plan.execGenerate(context);
                overallModel.add(model);

            FileOutputStream stream = null;
            try {
                stream = new FileOutputStream(outputTTLFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            overallModel.write(stream, "TURTLE") ;

            return;
        }  else {
            System.out.println("Usage: sparql-generate.jar <inputXMLFile.xml> <queryRQGFile.rqg> <outputTTLFile.ttl>");
        }
    }
}