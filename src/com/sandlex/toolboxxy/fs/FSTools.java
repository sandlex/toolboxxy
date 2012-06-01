package com.sandlex.toolboxxy.fs;


public class FSTools {

	public static void main(String[] args) {

		String cmd = args[0];
		Commands command = null;
		try {
			command = Commands.valueOf(cmd);
		} catch (IllegalArgumentException e) {
			System.out.println(String
							.format("Unrecognized command %s. Following commands are permitted:", cmd));

			for (Commands cd : Commands.values()) {
				System.out.println("\t" + cd);
			}

			System.exit(1);
		}

		delegateCommand(command, args);
	}
	
	private static void delegateCommand(Commands command, String[] args) {
		switch (command) {
		case mfts:
			new Mfts(args[1]).execute();
			break;
		case rfbd:
			new Rfbd(args[1]).execute();
			break;
		case sfdf:
			new Sfdf(args[1]).execute();
			break;
		case ctdc:
			new Ctdc(args[1], args[2]).execute();
			break;
		case ctds:
			new Ctds(args[1], args[2]).execute();
			break;
		}
		
		System.out.println("[Done]");
	}
	
}
