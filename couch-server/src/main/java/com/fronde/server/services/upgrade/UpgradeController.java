package com.fronde.server.services.upgrade;

import com.fronde.server.services.authorization.PublicAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/upgrade")
public class UpgradeController {

  @Autowired
  protected UpgradeService upgradeService;


  @RequestMapping(value = "/local/download/list", method = RequestMethod.GET)
  @ResponseBody
  public FileMetaDataList localDownloadList() {
    return upgradeService.localDownloadList();
  }

  @RequestMapping(value = "/local/restart/check", method = RequestMethod.GET)
  @ResponseBody
  public UpgradeResult restartCheck() {
    return upgradeService.restartCheck();
  }

  @RequestMapping(value = "/local/download/files", method = RequestMethod.POST)
  @ResponseBody
  public UpgradeResult localDownloadFiles() {
    return upgradeService.localDownloadFiles();
  }

  @RequestMapping(value = "/local/reset/files", method = RequestMethod.DELETE)
  @ResponseBody
  public UpgradeResult localResetFiles() {
    return upgradeService.localResetFiles();
  }

  @RequestMapping(value = "/local/upgrade", method = RequestMethod.POST)
  @ResponseBody
  public UpgradeResult upgradeRelease() {
    return upgradeService.upgradeRelease();
  }


  @RequestMapping(value = "/remote/download/list", method = RequestMethod.GET)
  @PublicAPI
  @ResponseBody
  public FileMetaDataList remoteDownloadList() {
    return upgradeService.remoteDownloadList();
  }

  @RequestMapping(value = "/remote/download/file/{file}", method = RequestMethod.POST)
  @PublicAPI
  @ResponseBody
  public byte[] remoteDownloadFile(@PathVariable("file") String file) {
    return upgradeService.remoteDownloadFile(file);
  }

}
