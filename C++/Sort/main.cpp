#include <iostream>
#include "SortTool.cpp"
#include<stdlib.h>
using namespace std;

int main()
{
  SortTool<int> *tool = new SortTool<int>;
  int src[100];
  for(int i = 0; i < 100; i++){
    src[i] = rand()%100;
  }
  tool->Qucik3Way(src, 0, 99);
  for(int i = 0; i < 100; i++){
    cout<<src[i]<<endl;
  }
  return 0;
}
