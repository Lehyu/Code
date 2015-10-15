#include "SortTool.h"

template<typename T>
SortTool<T>::SortTool(){};
template<typename T>
void SortTool<T>::_quick3Way(T *src, int lower, int upper)
{
  if(upper <= lower) return;
  int lt = lower;
  int gt = upper;
  int index = lower;
  T key = src[lower];
  while(index <= gt){
      if(key > src[index]){
        _exch(src, lt++, index++);
      }else if(key < src[index]){
        _exch(src, gt--, index);
      }else{
        index++;
      }
  }
  _quick3Way(src, lower, lt-1);
  _quick3Way(src, gt+1, upper);

}
template<typename T>
void SortTool<T>::_exch(T* src, int i, int j)
{
  T temp = src[i];
  src[i] = src[j];
  src[j] = temp;
}
template<typename T>
void SortTool<T>::Qucik3Way(T *src, int lower, int upper)
{
  _quick3Way(src, lower, upper);
}
