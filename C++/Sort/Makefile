# 1.\
edit:main.o SortTool.o \
	g++ -o edit main.o SortTool.o

#main.o:main.cpp SortTool.cpp \
	g++ -c main.cpp \
SortTool.o:SortTool.h \
	g++ -c SortTool.cpp \
clean: \
	rm edit	main.o SortTool.o

#improve
# 变量，隐晦规则，不要包含.o的.cpp文件，如
#main.o:main.cpp SortTool.cpp 可简化如下 \



objects = main.o SortTool.o

edit:$(objects)
	g++ -o edit $(objects)

*.o:*.cpp *.h
#nmain.o:SortTool.cpp
#	g++ -c main.cpp

#SortTool.o:SortTool.h
#	g++ -c SortTool.cpp
.PHONY : clean
clean:
	-rm edit $(objects)
