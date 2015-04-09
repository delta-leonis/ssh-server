#include "Watchdog.h"


void Watchdog::kick(float s)
{
    LPC_WDT->WDCLKSEL = 0x1;                // Set CLK src to PCLK
    uint32_t clk = SystemCoreClock / 16;    // WD has a fixed /4 prescaler, PCLK default is /4
    LPC_WDT->WDTC = s * (float)clk;
    LPC_WDT->WDMOD = 0x3;                   // Enabled and Reset
    kick();

}

void Watchdog::kick()
{
    LPC_WDT->WDFEED = 0xAA;
    LPC_WDT->WDFEED = 0x55;
}