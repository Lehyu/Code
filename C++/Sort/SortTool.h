#ifndef SORTTOOL_H_INCLUDED
#define SORTTOOL_H_INCLUDED
template<typename T> class SortTool
{
public:
  SortTool();
  void Qucik3Way(T *src, int lower, int upper);
 private:
  void _quick3Way(T *src, int lower, int upper);
  void _exch(T* src, int i, int j);
};


#endif // QUICK3WAY_H_INCLUDED
