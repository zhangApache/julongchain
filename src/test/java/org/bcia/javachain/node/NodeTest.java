package org.bcia.javachain.node;

import org.apache.commons.cli.ParseException;
import org.bcia.javachain.BaseJunit4Test;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class NodeTest extends BaseJunit4Test {
    @Autowired
    private Node node;

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void execCmd() throws ParseException {
        String[] caseArgs1 = new String[]{"channel", "create", "-c", "localhost:7050", "-ci", "mychannel", "-f", "filefile"};

        node.execCmd(caseArgs1);
    }
}