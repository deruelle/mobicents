These are the examples for Training Lab.


If you are porting training examples to Mobicents binaries, don't forget to add
1) Lab RA stage libraries to /examples/lib/labra
2) Lab RA stage libraries to /resources/labra
3) Following lines to /examples/lib/buil.xml in 'lib-init' target

	<target name="lib-init">
		.....
		....
		....
		<path id="labra-home.id">
			<pathelement location="${lib-prefix}/${labra}" />
		</path>
		<property name="labra-home" refid="labra-home.id" />
		....

		<path id="examples-libraries">
			.....
			....
			<!-- Lab RA -->
			<pathelement location="${lib-prefix}/${labra}/labra-event.jar" />												
			<pathelement location="${lib-prefix}/${labra}/labra-ratype-DU.jar" />
			<pathelement location="${lib-prefix}/${labra}/labra-ra-DU.jar" />
			<pathelement location="${lib-prefix}/${labra}/labra-ratype.jar" />
			<pathelement location="${lib-prefix}/${labra}/labra-ra.jar" />
			....
		</path>
		...	
	</target>
