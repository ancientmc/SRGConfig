## SRGConfig ##
This is an internal tool created for the Ancient Coder Pack (ACP). It isn’t really intended for end-users to use, but 
downloading and usage is permitted, and feedback is highly encouraged. 

SRGConfig is a tool that converts and tweaks RGS script files into SRG mappings. An RGS file is a RetroGuard script. 
RetroGuard is an old obfuscation/deobfuscation program for Java that was used in the earliest versions of the 
Mod Coder Pack. Eventually, starting around Beta 1.2_02 I believe, the MCP developers moved away from RGS and modified 
RetroGuard to use SRG files. SRG stands for Searge’s Retro Guard, which was used for the majority of MCP’s lifetime, and 
then by MinecraftForge in their MCPConfig tool, until the forge developers created TSRG files (I don’t know what the T 
stands for).

SRGConfig takes in the RGS files found in old MCP distributions and converts them into SRG files. The file conversion 
is done via a fork of a program called rgs2srg, originally developed by Jamie Mansfield (jamierocks). CSV files are also 
parsed via OpenCSV to fill in intermediary method and field names with human-readable names; these names are drawn from 
numerous MCP versions to ensure that as many names are filled in as possible.

# SEVERAL MCP VERSIONS WERE USED FOR THE NAMES IN THIS REPO. CREDITS TO THE MCP TEAM. SEE THE LICENSE FOR MORE INFORMATION.
