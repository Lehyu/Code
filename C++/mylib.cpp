#include<iostream>
#include<time.h>
using namespace std;

//reverse(ab) = ba
template <typename T>
void reverse(T *src, int beg, int end)
{
  while(beg<=end)
    {
      T tmp = src[beg];
      src[beg] = src[end];
      src[end] = tmp;
      beg++;
      end--;
    }
}
//acreverse(ab)  = ba
template<typename T>
void acreverse(T * src, int shift, int n)
{
  for(int i = 0; i < shift; i++)
    {
      T tmp = src[i];
      int before = i;
      int next = (before + shift) % n;
      while(next != i)
	{
	  src[before] = src[next];
	  before = next;
	  next += shift;
	  next %= n;
	}
      src[before] = tmp;
    }
}

/*binary search
  input: src(array), key(search key),len(the length of src)
  ouput: the index of key,if it didn't exist return -1
 */
template<typename T>
int bsearch(T *src, int key, int len)
{
  int low = 0;
  int high = len -1;
  while(low +1 != high)
    {
      int mid = (low + high)/2;
      if(src[mid] < key)
        low = mid;
      else
        high = mid;
    }
  if(src[high] == key)
    return high;
  else if(src[low] == key)
    return low;
  return -1;
}

template<typename T>
int bsearch_t(T *src, int key, int low, int high)
{
  if(low+1 != high)
    {
      int mid = (low + high)/2;
      if(src[mid] < key)
        bsearch_t(src, key, mid, high);
      else
        bsearch_t(src, key, low, mid);
    }
  else
    {
      if(src[high] == key)
        return high;
      else if(src[low] == key)
        return low;
      else
        return -1;
    }
}

#define LENGTH 1000000
#define SHIFT  500000
int main()
{
  char  src[10] = {'a','b','c','d','e','g','g','g','h','i'};
  char key;
  cin>>key;
  cout<<bsearch_t(src, key,0,9)<<endl;
  return 0;
}
