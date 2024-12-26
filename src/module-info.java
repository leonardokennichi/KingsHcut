module BadFinalProject {
	requires java.sql;
	requires javafx.graphics;
	requires javafx.controls;
	requires javafx.base;
	opens main;
	exports model;
}