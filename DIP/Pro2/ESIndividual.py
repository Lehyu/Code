import math
import random
import time

from bpnet import bpnet

class ESIndividual:
    """
    tar_vars is a list of target var
    steps is a list of standard deviation
    SD is a var of standard deviation
    delta is a var of S.D of each weight of tar_vars
    tar_vars = [nhl, nhmax, N]
    """
    def __init__(self, ni, no, tar_vars, steps, bound, SD, delta):
        self.ni = ni
        self.no = no
        self.tar_vars = tar_vars
        self.steps = steps
        self.bound = bound
        self.SD = SD
        self.delta = delta
        self.fitness = 0
        self.bp = bpnet(self.ni, int(round(self.tar_vars[0])), int(round(self.tar_vars[1])), self.no, self.tar_vars[2])

    def mutate_tar_vars(self):
        N = random.gauss
        for key, item in enumerate(self.tar_vars):
            self.tar_vars[key] = item + N(0, self.steps[key])
            while self.tar_vars[key] < self.bound[key][0] \
                    or self.tar_vars[key] > self.bound[key][1]:
                    self.tar_vars[key] = int(round(item + N(0, self.steps[key])))

    def mutate_adaptive(self):
        N = random.gauss
        for key, item in enumerate(self.steps):
            self.steps[key] = item*math.exp(N(0, self.SD) + N(0, self.delta))

    def generate(self):
        N = random.gauss
        mutate_steps = [item * math.exp(N(0, self.SD) + N(0, self.delta)) for item in self.steps]

        mutate_vars = []
        for key, item in enumerate(self.tar_vars):
            var = item + N(0, self.steps[key])
            while var < self.bound[key][0] \
                    or var > self.bound[key][1]:
                if 0 <= key<=1:
                    var = int(round(item + N(0, self.steps[key])))
                else:
                    var = item + N(0, self.steps[key])
            mutate_vars.append(var)
        return ESIndividual(self.ni, self.no, mutate_vars, mutate_steps, self.bound, self.SD, self.delta)

    def run(self, train_data):
        start = time.time()
        self.mutate_tar_vars()
        self.mutate_adaptive()
        self.bp.train(train_data, 4.1)
        self.fitness = time.time()-start

    def __repr__(self):
        return repr(self.fitness)

