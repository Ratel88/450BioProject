#!/usr/bin/perl
use strict;

unless (-d "html/JTVUserManual") {
	execute("");
	execute("cd JTVUserManual;xsltproc -o single.html ../../docbook-xsl/html/docbook.xsl JTVUserManual.xml");
	
	execute("cd JTVUserManual;xsltproc ../../docbook-xsl/html/chunk.xsl JTVUserManual.xml ");
	
	execute("cd JTVUserManual;xsltproc -o JTVUserManual.fo ../../docbook-xsl/fo/docbook.xsl JTVUserManual.xml ");
	execute("cd JTVUserManual;../../fop/fop.sh -fo JTVUserManual.fo -pdf JTVUserManual.pdf");
	execute("mkdir html");
	execute("mkdir html/JTVUserManual");
	execute("mv JTVUserManual/JTVUserManual.pdf html/JTVUserManual");
	execute("mv JTVUserManual/*.html html/JTVUserManual");
	execute("cp -r JTVUserManual/figures html/JTVUserManual");
}

unless (-d "html/JTVProgrammerGuide") {
	execute("");
	execute("cd JTVProgrammerGuide;xsltproc -o single.html ../../docbook-xsl/html/docbook.xsl JTVProgrammerGuide.xml");
	
	execute("cd JTVProgrammerGuide;xsltproc ../../docbook-xsl/html/chunk.xsl JTVProgrammerGuide.xml ");
	
	execute("cd JTVProgrammerGuide;xsltproc -o JTVProgrammerGuide.fo ../../docbook-xsl/fo/docbook.xsl JTVProgrammerGuide.xml ");
	execute("cd JTVProgrammerGuide;../../fop/fop.sh -fo JTVProgrammerGuide.fo -pdf JTVProgrammerGuide.pdf");
	execute("mkdir html");
	execute("mkdir html/JTVProgrammerGuide");
	execute("mv JTVProgrammerGuide/JTVProgrammerGuide.pdf html/JTVProgrammerGuide");
	execute("mv JTVProgrammerGuide/*.html html/JTVProgrammerGuide");
	execute("cp -r JTVProgrammerGuide/figures html/JTVProgrammerGuide");
}
sub execute {
	my $cmd = shift();
	print STDERR "$cmd\n";
	system($cmd);
}