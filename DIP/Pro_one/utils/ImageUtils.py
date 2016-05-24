from PIL import Image
import numpy
import math


def hist(path):
    image = Image.open(path)
    width, height = image.size
    nk = [0]*255
    print(nk)
    print(image.format)
    maxp = minp = image.getpixel((0, 0))
    for h in range(0, height):
        for w in range(0, width):
            pixel = image.getpixel((w, h))
            if pixel > maxp:
                maxp = pixel
            if pixel < minp:
                minp = pixel
            nk[int(pixel)] += 1

    total = width*height
    # hist before
    prs = [rk/total for rk in nk]
    # hist after
    for (index, rk) in enumerate(prs):
        if index != 0:
            prs[index] += prs[index - 1]

    tar_image = Image.new(image.mode, image.size)
    for h in range(0, height):
        for w in range(0, width):
            index = image.getpixel((w, h))
            pixel = int(prs[index]*(maxp-minp)+minp)
            tar_image.putpixel((w, h), pixel)
    tar_image.save('../image/hist.jpg')


def gauss_highpass_filter_c(path, d0):
    image = Image.open(path)
    pixels = numpy.asarray(image)
    pixels.flags.writeable = True
    width, height = image.size

    results = [[]] * (2*height)
    for y in range(0, 2 * height):
        col_result = []
        for x in range(0, 2 * width):
            if 0 <= x < width and 0 <= y < height:
                col_result.append(float(pixels[x, y])*math.pow(-1, x+y))
            else:
                col_result.append(0)
        results[y] = col_result
    results = numpy.fft.fft2(results)

    for u in range(0, 2*width):
        for v in range(0, 2*height):
            d = math.sqrt(math.pow(u - width, 2) + math.pow((v - height), 2))
            h = 1 - math.exp(-math.pow(d, 2) / (2 * math.pow(d0, 2)))
            results[u][v] *= h
    results = numpy.fft.ifft2(results)

    target = Image.new(image.mode, image.size)
    for x in range(0, width):
        for y in range(0, height):
            target.putpixel((x, y), abs(int(complex(results[x][y]).real)))
    target.save('../image/gauss.png')


# fftshit
def gauss_highpass_filter(path, d0):
    image = Image.open(path)
    pixels = numpy.asarray(image)
    pixels.flags.writeable = True
    width, height = image.size
    for x in range(0, width):
        for y in range(0, height):
            pixels[x, y] = float(pixels[x, y])
    pixels = numpy.fft.fft2(pixels)
    pixels = numpy.fft.fftshift(pixels)

    for u in range(0, width):
        for v in range(0, height):
            d = math.sqrt(math.pow(u-width/2, 2)+math.pow((v - height/2), 2))
            h = 1 - math.exp(-math.pow(d, 2)/(2*math.pow(d0, 2)))
            pixels[u, v] *= h
    pixels = numpy.fft.ifftshift(pixels)
    pixels = numpy.fft.ifft2(pixels)

    results = [[]]*height
    for v in range(0, height):
        col_result = []
        for u in range(0, width):
            pixel = abs(int(complex(pixels[u, v]).real))
            col_result.append(pixel)
        results[v] = col_result
    print(results)

    target = Image.new(image.mode, image.size)
    for x in range(0, width):
        for y in range(0, height):
            target.putpixel((x,y), results[x][y])
    target.save('../image/gauss_using_fftshift.jpg')


def median_adaptive_filter(path, maxsize):
    image = Image.open(path)
    width, height = image.size
    for h in range(0, height):
        for w in range(0, width):
            pixel = find_adaptive_pixel(image, maxsize, w, h)
            image.putpixel((w, h), pixel)

    image.save('../image/median_adaptive.jpg')


def find_adaptive_pixel(image, maxsize, w, h):
    sizes = [(2 * i + 1) for i in range(1, int((maxsize - 3) / 2) + 2)]
    for size in sizes:
        half = int(size / 2)
        width, height = image.size
        left, right, top, bottom = find_rect(w, h, width, height, half)
        s = get_area_pixels(image, left, right, top, bottom)
        median = get_median(s)
        if s[0] < median < s[-1]:
            pixel = image.getpixel((w, h))
            if not (s[0] < pixel < s[-1]):
                return median
            else:
                return pixel
        elif size == maxsize:
            return median


def get_area_pixels(image, left, right, top, bottom):
    s = []
    for i in range(top, bottom + 1):
        for j in range(left, right + 1):
            try:
                s.append(image.getpixel((j, i)))
            except IndexError:
                print(top, bottom + 1, left, right + 1)
                exit()
    s.sort()
    return s


def median_filter(path, size):
    image = Image.open(path)
    width, height = image.size
    half = int(size/2)
    for h in range(0, height):
        for w in range(0, width):
            left, right, top, bottom = find_rect(w, h,  width, height, half)
            s = get_area_pixels(image, left, right, top, bottom)
            median = get_median(s)
            image.putpixel((w, h), int(median))
    image.save('../image/median.jpg')


def get_median(s):
    mid = int(len(s) / 2)
    median = s[mid]
    if len(s) % 2 == 0:
        try:
            median = int((s[mid] + s[mid - 1]) / 2)
        except TypeError:
            print(len(s), len(s) / 2)
    return median


def find_rect(w, h, width, height, half):
    right = w + half if (w + half) < width else width - 1
    left = w - half if (w - half) > 0 else 0
    top = h - half if (h - half) > 0 else 0
    bottom = h + half if (h + half) < height else height - 1
    return left, right, top, bottom

