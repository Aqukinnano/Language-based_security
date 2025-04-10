Report

Please, submit a report that includes in addition to your answers to the questions above:

    Part 0: your ShoppingCart.java with clear instructions on how to compile and run it.
    首先进入ShoppingCart.java所在目录，输入命令make all编译，再输入java ShoppingCart运行。
    （ShoppingCart.java已经以附件的形式与报告一同提交。）

    Part 1: the output logs and explanations how to get them
    为了稳定地模拟TOCTOU攻击，我们在main函数中wallet.getBalance()和wallet.setBalance()之间人为设置了断点。
    此时我们分别用在两个终端中运行程序，先在终端1中输入car进行购买，使程序进入上述的TOCTOU的攻击窗口，再在终端2中输入car进行购买，随后在两个终端中都输入enter继续。如图所示，检查pocket.txt，发现其中出现了两个car，这说明两次购买操作都成功了，TOCTOU攻击成功。
    共享资源是backEnd/wallet.txt文件（通过Wallet类访问）和backEnd/pocket.txt文件（通过Pocket类访问）。共享者是同一用户运行ShoppingCart程序的多个实例。由于文件系统是共享的，多个进程可以同时访问这些文件。
    此漏洞的根源问题在于，检查余额和扣款并非一个原子操作，在终端1检查余额通过后，程序并未实际扣款，此时终端2中的余额检查仍然会通过，因此两次购买都可以成功。

    Part 2: the entire code that secures buggy API(s) as well as the screenshots before and after the implemented security patch
    我们在Wallet.java中引入FileChannel和FileLock，添加一个safeWithdraw方法，使用文件锁确保检查和扣款是原子操作。并在检查和扣款之间人为设置了像Part1中那样的断点。
    此时再编译并运行程序，先在终端1中输入car进行购买，使程序进入TOCTOU的攻击窗口，再在终端2中输入car尝试进行购买。如图所示，此时我们可以发现终端2中的程序并没有进入攻击窗口，而是在等待终端1中的程序释放文件锁。此时我们在终端1中输入enter继续，终端2会自动因为余额检查不通过而退出。检查pocket.txt，发现其中只有一个car，这说明只有终端1中的程序购买成功，而终端2失败，TOCTOU攻击失败。、

    其他潜在竞态条件：Pocket类的addProduct方法需要考虑并发写入时的数据完整性问题，也应添加文件锁保护，防止出现商品记录交错或者覆盖丢失。此外，虽然实验中Pocket类只有add操作，但如果未来添加删除或修改商品的功能，则也需要完整性保护。

为什么这些保护足够：

使用文件锁确保检查和扣款是原子操作
锁的范围仅限于关键操作（余额检查和扣款）
锁在操作完成后立即释放（try-with-resources）

为什么不过度：

只在真正需要同步的地方加锁（Wallet和Pocket的文件操作）
不锁定整个应用程序或长时间持有锁
锁的粒度精细（每个文件独立锁定）

性能考虑：

文件锁是操作系统级别的，效率较高
锁持有时间极短，仅包含必要的文件操作
不影响程序的并发性，不同用户可以同时访问不同部分

（修改后的ShoppingCart_1.java，Wallet_1.java，Pocket_1.java已经以附件的形式与报告一同提交，如果需要测试，请把文件名中的_1后缀删除。）
