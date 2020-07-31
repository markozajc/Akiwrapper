package com.markozajc.akiwrapper.core.utils;

import com.markozajc.akiwrapper.core.Route;

public class Playground {

	public static void main(String[] args) throws Throwable {
		System.out.println(Route.UNIREST.config().getDefaultHeaders());
		System.out.println(Route.UNIREST.get(
		    "https://en.akinator.com/new_session?callback=jQuery341041289490420633124_1596196964877&urlApiWs=https%3A%2F%2Fsrv13.akinator.com%3A9400%2Fws&player=website-desktop&partner=1&uid_ext_session=bde1684a-d325-11ea-811c-0cc47adc2898&frontaddr=MTUxLjgwLjM2LjEzOA%3D%3D&childMod=&constraint=ETAT%3C%3E%27AV%27&soft_constraint=&question_filter=&_=1596196964878")
		    .asString()
		    .getBody());
	}

}
