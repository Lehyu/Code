from EvolutionaryStrategy import EvolutionaryStrategy
import numpy
import csv

#model: f(x1,x2,x3,...,xn)=x1*x1+x2*x2+x3*x3+....+xn
def dec2bin(str_number):
    number = int(str_number.strip())
    results = []
    while True:
        if number == 0: break
        number, rem = divmod(number, 2)
        results.append(rem)
    return results

def loaddata(path, fixlen):
    data = []
    # line = 0,0,0,...0|10
    for line in open(path):
        try:
            inputs, results = line.split('|')
            inputs = [float(key.strip()) for key in inputs.split(',')]
            results = dec2bin(results)
            results = results+[0]*(fixlen - len(results))
            record = []
            record.append(inputs)
            record.append(results)
            data.append(record)
        except Exception:
            pass

    return data

no = 4
train_data = loaddata('Train.txt', 4)
print(train_data)
ni = len(train_data[0][0])
vars_len = 3
bound = [
    [1,4],
    [5,20],
    [0.1,2]
]
pop_sizes = [5, 10]
gens=[50]
best = 0
for pop_size in pop_sizes:
    for MAX_GEN in gens:
        print('pop_size, max_gen',pop_size, MAX_GEN)
        es = EvolutionaryStrategy(pop_size=pop_size, ni=ni, no=no, vars_len=vars_len, init_SD=3.0,
                                  bound=bound, MAX_GEN=MAX_GEN, data=train_data)
        es.run()
        if es.population[0].fitness < best.fitness:
            best = es.population[0]

bp = best.bp
test_data = loaddata('Test.txt')
bp.test(test_data)
best.save('config.json')
print('done')








