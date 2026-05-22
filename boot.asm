
# kernel/boot.asm

```asm
[bits 32]
[extern kernel_main]

call kernel_main
jmp $
```

---