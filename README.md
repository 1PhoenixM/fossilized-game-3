If running in NetBeans:
  File not found error can occur. Solution: move 
      magic.txt
        to src's parent folder.
        
If running in terminal / command prompt:
  Class not found error may occur. Solution: in every
    .java file at the top is a line that NetBeans uses, which causes this class error:
      package Fossilized;
        Comment these out and save.
