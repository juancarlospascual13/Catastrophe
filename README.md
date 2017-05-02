# Catastrophe
Catastrophe aims to simulate an environment to test how MA-PDDL planning algorithms fare in somewhat real situations.
At this point it works only with CMAP[http://www.plg.inf.uc3m.es/~dborrajo/map.html] and all filesystem routes are hardcoded.
This is part of my work for the subject Advanced Telematic Applications at UPM's ETSIST.

Instructions:
-The path to a json file with a problem, such as the ones found in "save", must be passed as the first argument.
-On the config file, cmap must point to the path with your CMAP installation.
-On the config file, output must point to a fresh directory to store info.

PD: This project uses Lombok and Jackson.
