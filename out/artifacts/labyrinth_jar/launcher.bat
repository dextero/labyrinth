set JAVA_32BIT="c:\program files (x86)\java\jre7\bin\java"

if not exist ("%USERPROFILE%\.fmj.registry.xml") (
    :: tworzenie rejestru urzadzen FMJ
    %JAVA_32BIT% ^
        -Djava.library.path="native/win32-x86" ^
        -Dfile.encoding=UTF-8 ^
        -classpath "labyrinth.jar;lib/fmj.jar;lib/jdom.jar;lib/lti-civil-no_s_w_t.jar;lib/jl1.0.jar;lib/tritonus_share.jar;lib/mp3spi1.9.4.jar;lib/jorbis-0.0.15.jar;lib/jogg-0.0.7.jar;lib/vorbisspi1.0.2.jar;lib/jspeex.jar;lib/jna.jar;lib/ffmpeg-java.jar;lib/theora-java.jar;lib/jheora-patch.jar" ^
        FMJAutoConfig
)

:: wlasciwe uruchamianie
%JAVA_32BIT% ^
    -Djava.library.path="native/win32-x86" ^
    -Dfile.encoding=UTF-8 ^
    -jar .\labyrinth.jar
