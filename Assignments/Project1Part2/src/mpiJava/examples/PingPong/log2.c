int log2(int n)
{
    int log2_n, i;

    log2_n = 0;
    i      = n;
    while (i > 1) {
       log2_n = log2_n+1;
       i = i/2;
    }
    return log2_n;
}
