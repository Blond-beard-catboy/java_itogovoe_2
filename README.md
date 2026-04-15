# java_itogovoe_2
# type in powershell(don't need administrator permissions):
Set-ItemProperty HKCU:\Console VirtualTerminalLevel -Type DWORD 1

# then type:
1. &"C:\Users\ВашеИмя\.jdks\openjdk-21.0.2\bin\javac" -d target src\main\java\org\example\*.java
2. &"C:\Users\ВашеИмя\.jdks\openjdk-21.0.2\bin\java" -cp target org.example.Main


# or you can start in cmd WITHOUT animation:
1. javac -d target src/main/java/org/example/*.java
2. java -cp target org.example.Main
