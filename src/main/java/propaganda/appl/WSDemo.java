/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package propaganda.appl;

import fpg.sundry.Appl;
import static fpg.sundry.Appl.decodeArgs;
import static fpg.sundry.Appl.main;
import fpg.sundry.S;
import java.util.ArrayList;
import java.util.List;
import propaganda.server.HttpWSService;
import propaganda.server.HttpWSServive;

/**
 *
 * @author lars
 */
public class WSDemo extends Appl {
    @Override
    public void main() {
        HttpWSService s = new HttpWSService();
        final HttpWSServive port = s.getHttpWSServiceSOAPHttpPort();
        ArrayList<String> list = new ArrayList<String>();
        list.add("_ @;list");
        final List<String> rl = port.processDatagram(list, 1000);
        System.out.println("GOT: " + rl);
    }

    public static void main(String[] args) {
	decodeArgs(args);
	main(new WSDemo());
    }
}